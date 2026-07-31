package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platz;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.ImportiereWochenplan;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prüft, dass die erneute Übernahme desselben Wochenplans bestehende Saalpläne — und damit bereits
 * verkaufte Plätze — nicht überschreibt. Der Import legt einen leeren Saalplan nur für neue
 * Vorstellungen an; für bekannte bleibt der vorhandene (inkl. Belegung) unangetastet.
 */
@SpringBootTest
class WochenplanImportIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private ImportiereWochenplan importiereWochenplan;

    @Autowired
    private SaalplanStapel saalplanStapel;

    @Autowired
    private AktuelleVorstellungen aktuelleVorstellungen;

    @Test
    void fuer_wochenplanErneutUebernommen_erhaeltVerkauftePlaetze() {
        // arrange: einen bislang freien Platz verkaufen (die deterministische Demo-Belegung lässt ihn frei)
        var vorstellungId = aktuelleVorstellungen.alleVorstellungen().get(0).getId();
        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);
        var freierPlatz = ersterFreierPlatz(saalplan.getPlaetze().values().stream()
                .flatMap(reihe -> reihe.values().stream())
                .toList());
        saalplan.markiereAlsVerkauft(new ZusammenhaengendePlaetze(List.of(freierPlatz.getId())));
        saalplanStapel.legeZurueck(saalplan);

        // act
        importiereWochenplan.fuer(2025, 12);

        // assert
        var nachImport = saalplanStapel.holeSaalplan(vorstellungId);
        assertThat(nachImport.platz(freierPlatz.getId()).istVerkauft()).isTrue();
    }

    private Platz ersterFreierPlatz(List<Platz> plaetze) {
        return plaetze.stream()
                .filter(Platz::istFrei)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Kein freier Platz im Saalplan vorhanden"));
    }
}
