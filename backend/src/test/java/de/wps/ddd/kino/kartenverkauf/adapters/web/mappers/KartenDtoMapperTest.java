package de.wps.ddd.kino.kartenverkauf.adapters.web.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.KartenDtoMapper;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KinokarteId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KartenDtoMapperTest {

    private final KartenDtoMapper mapper = new KartenDtoMapper();

    private Kinokarte kinokarte(int reihe, int platz) {
        return new Kinokarte(
                KinokarteId.neu(),
                Auftragsnummer.neueAuftragsnummer(),
                new VorstellungId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new Film("The Fast and the Curious"),
                new Beginn(LocalDateTime.parse("2025-03-23T14:30")),
                new Saal("kleiner Saal"),
                new ReiheNummer(reihe),
                new PlatzNummer(platz));
    }

    @Test
    void toDto_einzelneKinokarte_mapptAlleFelder() {
        // arrange
        var kinokarte = kinokarte(4, 2);

        // act
        var dto = mapper.toDto(kinokarte);

        // assert
        assertThat(dto.film()).isEqualTo("The Fast and the Curious");
        assertThat(dto.beginn()).isEqualTo("2025-03-23T14:30");
        assertThat(dto.saal()).isEqualTo("kleiner Saal");
        assertThat(dto.reihe()).isEqualTo(4);
        assertThat(dto.platz()).isEqualTo(2);
    }

    @Test
    void toDto_liste_mapptJedeKinokarte() {
        // arrange
        var kinokarten = List.of(kinokarte(4, 1), kinokarte(4, 2));

        // act
        var dtos = mapper.toDto(kinokarten);

        // assert
        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(dto -> dto.platz()).containsExactly(1, 2);
    }
}
