package de.wps.ddd.kino.zahlung.application;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.zahlung.StarteZahlungDto;
import de.wps.ddd.kino.zahlung.ZahlungAbgebrochenDto;
import de.wps.ddd.kino.zahlung.ZahlungEingegangenDto;
import de.wps.ddd.kino.zahlung.domain.Betrag;
import de.wps.ddd.kino.zahlung.domain.Zahlung;
import de.wps.ddd.kino.zahlung.domain.Zahlungen;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulierter Zahlungsdienstleister. Registriert eine offene Zahlung und wickelt sie auf Anforderung ab
 * (innerhalb von 3–6 s, in 2 von 3 Fällen erfolgreich).
 */
@Service
@ApplicationService
@RequiredArgsConstructor
public class Zahlungsabwicklung {

    private final Zahlungen zahlungen;

    private final ApplicationEventPublisher events;

    private final TransactionTemplate transactionTemplate;

    @ApplicationModuleListener
    public void verarbeite(StarteZahlungDto command) {
        zahlungen.speichere(Zahlung.fuer(
                new Zahlungsreferenz(command.referenz()),
                new Betrag(command.betragInCent())));
    }

    @Async
    public void bezahle(Zahlungsreferenz referenz) {
        // Die simulierte Bearbeitungsdauer liegt bewusst außerhalb der Transaktion: erst warten,
        // dann den Statuswechsel + das Ergebnis-Event transaktional festschreiben. Die Transaktion
        // ist Voraussetzung dafür, dass der nachgelagerte @ApplicationModuleListener (AFTER_COMMIT)
        // feuert; programmatisch per TransactionTemplate, da der @Async-Aufruf keinen Proxy durchläuft.
        warte(bearbeitungsdauerMillis());
        boolean erfolgreich = zahlungErfolgreich();
        transactionTemplate.executeWithoutResult(status -> schliesseAb(referenz, erfolgreich));
    }

    private void schliesseAb(Zahlungsreferenz referenz, boolean erfolgreich) {
        var zahlung = zahlungen.hole(referenz);
        if (erfolgreich) {
            zahlungen.speichere(zahlung.eingegangen());
            events.publishEvent(new ZahlungEingegangenDto(referenz.wert()));
        } else {
            zahlungen.speichere(zahlung.abgebrochen());
            events.publishEvent(new ZahlungAbgebrochenDto(referenz.wert()));
        }
    }

    private void warte(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    long bearbeitungsdauerMillis() {
        return 3000 + ThreadLocalRandom.current().nextInt(3001);
    }

    boolean zahlungErfolgreich() {
        return ThreadLocalRandom.current().nextInt(3) != 0;
    }
}
