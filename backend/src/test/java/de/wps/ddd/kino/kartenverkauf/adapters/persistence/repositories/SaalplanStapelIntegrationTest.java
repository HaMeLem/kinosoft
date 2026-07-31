package de.wps.ddd.kino.kartenverkauf.adapters.persistence.repositories;


import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platz;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzKategorie;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SaalplanStapelIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private SaalplanStapel saalplanStapel;

    @Test
    void holeSaalplan_bekannteVorstellung_liefertSaalplan() {
        // arrange
        var vorstellungId = vorstellungId("Star Boars", "kleiner Saal", LocalDateTime.of(2025, 3, 18, 15, 30));

        // act
        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);

        // assert
        assertThat(saalplan).isNotNull();
        assertThat(saalplan.getVorstellungId()).isEqualTo(vorstellungId);
        int reihenAnzahl = 4;
        int platzAnzahlInReihe = 8;
        List<ReiheNummer> reihen = saalplan.getPlaetze().keySet().stream().toList();
        assertThat(reihen).hasSize(reihenAnzahl);
        saalplan.getPlaetze().forEach((reihe, plaetzeListe) -> assertThat(plaetzeListe).hasSize(platzAnzahlInReihe));
    }

    @Test
    void holeSaalplan_unbekannteVorstellung_wirftRessourceNichtGefunden() {
        // arrange
        var unbekannteVorstellungId = new VorstellungId(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> saalplanStapel.holeSaalplan(unbekannteVorstellungId))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert nicht");
    }

    @Test
    void legeZurueck_geaenderterSaalplan() {
        // arrange
        var vorstellungId = vorstellungId("Star Boars", "kleiner Saal", LocalDateTime.of(2025, 3, 18, 15, 30));
        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);
        var platzId = new PlatzId(new ReiheNummer(2), new PlatzNummer(3));
        assertThat(saalplan.platz(platzId).istVerkauft()).isFalse();

        // act
        saalplan.platz(platzId).markiereAlsVerkauft();
        saalplanStapel.legeZurueck(saalplan);

        // assert
        var geaenderterSaalplan = saalplanStapel.holeSaalplan(vorstellungId);
        assertThat(geaenderterSaalplan.platz(platzId).istVerkauft()).isTrue();
    }

    @Test
    void legeZurueck_neuerSaalplan() {
        // arrange
        var vorstellungId = new VorstellungId(UUID.randomUUID());
        var platzId = new PlatzId(new ReiheNummer(1), new PlatzNummer(1));
        var platzKategorie = PlatzKategorie.Loge;
        var saalplan = new Saalplan(vorstellungId, List.of(
                new Platz(platzId, platzKategorie, true)));

        // act
        saalplanStapel.legeZurueck(saalplan);

        // assert
        var neuerSaalplan = saalplanStapel.holeSaalplan(vorstellungId);
        assertThat(neuerSaalplan).isNotNull();
        assertThat(neuerSaalplan.platz(platzId).getKategorie()).isEqualTo(PlatzKategorie.Loge);
        assertThat(neuerSaalplan.platz(platzId).istVerkauft()).isTrue();
    }
}