package de.wps.ddd.kino.zahlung.application;

import de.wps.ddd.kino.zahlung.StarteZahlungDto;
import de.wps.ddd.kino.zahlung.ZahlungAbgebrochenDto;
import de.wps.ddd.kino.zahlung.ZahlungEingegangenDto;
import de.wps.ddd.kino.zahlung.domain.Betrag;
import de.wps.ddd.kino.zahlung.domain.Zahlung;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import de.wps.ddd.kino.zahlung.domain.Zahlungsstatus;
import de.wps.ddd.kino.zahlung.persistence.ZahlungenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ZahlungsabwicklungTest {

    private final ZahlungenRepository zahlungen = new ZahlungenRepository();
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final TransactionTemplate transactionTemplate = transactionTemplateDerDirektAusfuehrt();

    private final Zahlungsreferenz referenz = new Zahlungsreferenz(UUID.randomUUID());

    @Test
    void verarbeite_legtOffeneZahlungAn() {
        // act
        abwicklungMitErgebnis(true).verarbeite(new StarteZahlungDto(referenz.wert(), 5000));

        // assert
        var zahlung = zahlungen.hole(referenz);
        assertThat(zahlung.getStatus()).isEqualTo(Zahlungsstatus.Offen);
        assertThat(zahlung.getBetrag()).isEqualTo(new Betrag(5000));
    }

    @Test
    void bezahle_erfolg_setztEingegangenUndPubliziert() {
        // arrange
        zahlungen.speichere(Zahlung.fuer(referenz, new Betrag(5000)));

        // act
        abwicklungMitErgebnis(true).bezahle(referenz);

        // assert
        assertThat(zahlungen.hole(referenz).getStatus()).isEqualTo(Zahlungsstatus.Eingegangen);
        verify(events).publishEvent(new ZahlungEingegangenDto(referenz.wert()));
    }

    @Test
    void bezahle_fehlschlag_setztAbgebrochenUndPubliziert() {
        // arrange
        zahlungen.speichere(Zahlung.fuer(referenz, new Betrag(5000)));

        // act
        abwicklungMitErgebnis(false).bezahle(referenz);

        // assert
        assertThat(zahlungen.hole(referenz).getStatus()).isEqualTo(Zahlungsstatus.Abgebrochen);
        verify(events).publishEvent(new ZahlungAbgebrochenDto(referenz.wert()));
    }

    // Abwicklung mit deterministischem Ergebnis und ohne Wartezeit (Test-Seams überschrieben)
    private Zahlungsabwicklung abwicklungMitErgebnis(boolean erfolgreich) {
        return new Zahlungsabwicklung(zahlungen, events, transactionTemplate) {
            @Override
            long bearbeitungsdauerMillis() {
                return 0;
            }

            @Override
            boolean zahlungErfolgreich() {
                return erfolgreich;
            }
        };
    }

    // Ersetzt die (im Unit-Test fehlende) Spring-Transaktion: führt den Callback einfach direkt aus.
    private static TransactionTemplate transactionTemplateDerDirektAusfuehrt() {
        var template = mock(TransactionTemplate.class);
        doAnswer(invocation -> {
            invocation.<Consumer<TransactionStatus>>getArgument(0).accept(null);
            return null;
        }).when(template).executeWithoutResult(any());
        return template;
    }
}
