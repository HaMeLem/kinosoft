package de.wps.ddd.kino.kartenverkauf.adapters.persistence.repositories;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AktuelleVorstellungenIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private AktuelleVorstellungen aktuelleVorstellungen;

    @Test
    void alleVorstellungen_liefertVorstellungen() {
        // act
        var vorstellungen = aktuelleVorstellungen.alleVorstellungen();

        // assert
        assertThat(vorstellungen).isNotEmpty();
    }

    @Test
    void holeVorstellung_bekannteVorstellung_liefertVorstellung() {
        // arrange
        var vorstellungId = vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));

        // act
        var vorstellung = aktuelleVorstellungen.holeVorstellung(vorstellungId);

        // assert
        assertThat(vorstellung.getId()).isEqualTo(vorstellungId);
        assertThat(vorstellung.getFilm().name()).isEqualTo("The Fast and the Curious");
        assertThat(vorstellung.getSaal().name()).isEqualTo("kleiner Saal");
    }

    @Test
    void holeVorstellung_unbekannteVorstellung_wirftRessourceNichtGefunden() {
        // arrange
        var unbekannteVorstellungId = new VorstellungId(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> aktuelleVorstellungen.holeVorstellung(unbekannteVorstellungId))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert nicht");
    }
}
