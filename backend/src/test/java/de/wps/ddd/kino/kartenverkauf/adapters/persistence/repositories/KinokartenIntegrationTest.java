package de.wps.ddd.kino.kartenverkauf.adapters.persistence.repositories;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KinokarteId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Kinokarten;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KinokartenIntegrationTest {

    @Autowired
    private Kinokarten kinokarten;

    private final VorstellungId vorstellungId = new VorstellungId(UUID.randomUUID());

    @Test
    void speichereUndFinde_liestDieKartenEinesAuftrags() {
        // arrange
        var auftragsnummer = Auftragsnummer.neueAuftragsnummer();
        var karten = List.of(kinokarte(auftragsnummer, 4, 1), kinokarte(auftragsnummer, 4, 2));

        // act
        kinokarten.speichere(karten);
        var gefunden = kinokarten.finde(auftragsnummer);

        // assert
        assertThat(gefunden).hasSize(2);
        assertThat(gefunden).allSatisfy(karte -> {
            assertThat(karte.getAuftragsnummer()).isEqualTo(auftragsnummer);
            assertThat(karte.getVorstellungId()).isEqualTo(vorstellungId);
            assertThat(karte.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
            assertThat(karte.getReihe()).isEqualTo(new ReiheNummer(4));
        });
        assertThat(gefunden).extracting(karte -> karte.getPlatz().nummer())
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void finde_unbekannteAuftragsnummer_liefertLeereListe() {
        // arrange
        var unbekannteAuftragsnummer = Auftragsnummer.neueAuftragsnummer();

        // act / assert
        assertThat(kinokarten.finde(unbekannteAuftragsnummer)).isEmpty();
    }

    private Kinokarte kinokarte(Auftragsnummer auftragsnummer, int reihe, int platz) {
        return new Kinokarte(
                KinokarteId.neu(),
                auftragsnummer,
                vorstellungId,
                new Film("The Fast and the Curious"),
                new Beginn(LocalDateTime.parse("2025-03-23T14:30")),
                new Saal("kleiner Saal"),
                new ReiheNummer(reihe),
                new PlatzNummer(platz));
    }
}
