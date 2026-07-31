package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platzanzahl;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.SucheZusammenhaengendePlaetze;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SucheZusammenhaengendePlaetzeIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private SucheZusammenhaengendePlaetze sucheZusammenhaengendePlaetze;

    @Test
    void sucheZusammenhaengendePlaetze_vierPlaetze_liefertViererBlockInReihe4() {
        // arrange
        var vorstellungId = vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));

        // act
        var plaetze = sucheZusammenhaengendePlaetze.fuer(vorstellungId, new Platzanzahl(4));

        // assert
        assertThat(plaetze.plaetze()).hasSize(4);
        assertThat(plaetze.plaetze()).allMatch(p -> p.reihe().nummer() == 4);
        assertThat(plaetze.plaetze()).extracting(p -> p.platz().nummer()).containsExactly(1, 2, 3, 4);
    }

    @Test
    void sucheZusammenhaengendePlaetze_mehrAlsEineReihe_liefertLeereListe() {
        // arrange — eine Reihe fasst 8 Plätze, 9 zusammenhängende gibt es nie
        var vorstellungId = vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));

        // act
        var plaetze = sucheZusammenhaengendePlaetze.fuer(vorstellungId, new Platzanzahl(9));

        // assert
        assertThat(plaetze.plaetze()).isEmpty();
    }
}
