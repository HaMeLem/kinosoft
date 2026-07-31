package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungKategorie;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KartenBlockTest {

    private final KartenBlock kartenBlock = new KartenBlock();

    @Test
    void erstelleKarten_erzeugtEineKarteProPlatzMitVorstellungsdaten() {
        // arrange
        var vorstellung = new Vorstellung(
                new VorstellungId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new Saal("kleiner Saal"),
                new Beginn(LocalDateTime.parse("2025-03-23T14:30")),
                new Film("The Fast and the Curious"),
                VorstellungKategorie.Standard);
        var p1 = new PlatzId(new ReiheNummer(4), new PlatzNummer(1));
        var p2 = new PlatzId(new ReiheNummer(4), new PlatzNummer(2));
        var gewaehltePlaetze = new ZusammenhaengendePlaetze(List.of(p1, p2));
        var auftragsnummer = Auftragsnummer.neueAuftragsnummer();

        // act
        var karten = kartenBlock.erstelleKarten(auftragsnummer, vorstellung, gewaehltePlaetze);

        // assert
        assertThat(karten).hasSize(2);
        assertThat(karten).allSatisfy(karte -> {
            assertThat(karte.getId()).isNotNull();
            assertThat(karte.getAuftragsnummer()).isEqualTo(auftragsnummer);
            assertThat(karte.getVorstellungId()).isEqualTo(vorstellung.getId());
            assertThat(karte.getFilm()).isEqualTo(vorstellung.getFilm());
            assertThat(karte.getBeginn()).isEqualTo(vorstellung.getBeginn());
            assertThat(karte.getSaal()).isEqualTo(vorstellung.getSaal());
            assertThat(karte.getReihe()).isEqualTo(new ReiheNummer(4));
        });
        assertThat(karten).extracting(karte -> karte.getPlatz().nummer()).containsExactly(1, 2);
    }

    @Test
    void erstelleKarten_vergibtJeKarteEineEigeneKinokarteId() {
        // arrange
        var vorstellung = new Vorstellung(
                new VorstellungId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new Saal("kleiner Saal"),
                new Beginn(LocalDateTime.parse("2025-03-23T14:30")),
                new Film("The Fast and the Curious"),
                VorstellungKategorie.Standard);
        var gewaehltePlaetze = new ZusammenhaengendePlaetze(List.of(
                new PlatzId(new ReiheNummer(4), new PlatzNummer(1)),
                new PlatzId(new ReiheNummer(4), new PlatzNummer(2))));

        // act
        var karten = kartenBlock.erstelleKarten(Auftragsnummer.neueAuftragsnummer(), vorstellung, gewaehltePlaetze);

        // assert
        assertThat(karten).extracting(Kinokarte::getId).doesNotHaveDuplicates();
    }
}
