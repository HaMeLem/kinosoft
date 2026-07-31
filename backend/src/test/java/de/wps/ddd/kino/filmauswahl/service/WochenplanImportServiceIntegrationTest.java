package de.wps.ddd.kino.filmauswahl.service;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prüft, dass die erneute Übernahme desselben Wochenplans die Vorstellungen der Filmauswahl nicht
 * dupliziert: Der Import ist ein idempotenter Upsert über die wochenplanung-UUID.
 */
@SpringBootTest
class WochenplanImportServiceIntegrationTest extends IntegrationTestMitWochenplan {

    private static final LocalDate TAG_MIT_VORSTELLUNGEN = LocalDate.parse("2025-03-19");

    @Autowired
    private WochenplanImportService wochenplanImportService;

    @Autowired
    private ProgrammService programmService;

    @Test
    void importiere_wochenplanErneutUebernommen_dupliziertVorstellungenNicht() {
        // arrange
        var vorstellungenVorher = anzahlVorstellungen(TAG_MIT_VORSTELLUNGEN);

        // act
        wochenplanImportService.importiere(2025, 12);

        // assert
        assertThat(anzahlVorstellungen(TAG_MIT_VORSTELLUNGEN)).isEqualTo(vorstellungenVorher);
    }

    private int anzahlVorstellungen(LocalDate tag) {
        return programmService.holeVorstellungenFuerTag(tag).stream()
                .mapToInt(film -> film.getVorstellungen().size())
                .sum();
    }
}
