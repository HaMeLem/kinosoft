package de.wps.ddd.kino.kartenverkauf.application.fixtures;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.WochenplanUebernommen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Ergänzt nach dem {@link WochenplanUebernommen}-Ereignis eine Demo-Belegung auf den (vom Import leer
 * angelegten) Saalplänen: markiert ~25 % der Plätze als verkauft. Reine Seed-/Demo-Daten — bewusst
 * getrennt vom Import, der nur die empfangenen Daten transformiert.
 * <p>
 * Läuft async nach dem Commit des Imports (Saalpläne existieren dann) in einer eigenen Transaktion.
 * Der feste Seed macht die Belegung deterministisch und reproduzierbar.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaalplanBelegungFixture {

    private static final long BELEGUNG_SEED = 42;

    private final AktuelleVorstellungen aktuelleVorstellungen;
    private final SaalplanStapel saalplanStapel;

    @ApplicationModuleListener
    public void verarbeite(WochenplanUebernommen ereignis) {
        var vorstellungen = aktuelleVorstellungen.alleVorstellungen();
        log.info("Erzeuge Demo-Belegung für {} Saalpläne...", vorstellungen.size());

        for (var vorstellung : vorstellungen) {
            var saalplan = saalplanStapel.holeSaalplan(vorstellung.getId());
            belege(saalplan);
            saalplanStapel.legeZurueck(saalplan);
        }
    }

    private void belege(Saalplan saalplan) {
        var random = new Random(BELEGUNG_SEED);
        saalplan.getPlaetze().values().forEach(reihe -> reihe.values().forEach(platz -> {
            if (random.nextInt(4) == 0) {
                platz.markiereAlsVerkauft();
            }
        }));
    }
}
