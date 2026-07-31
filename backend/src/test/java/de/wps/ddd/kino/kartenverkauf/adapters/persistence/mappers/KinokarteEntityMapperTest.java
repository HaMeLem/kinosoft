package de.wps.ddd.kino.kartenverkauf.adapters.persistence.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers.KinokarteEntityMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model.KinokarteEntity;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KinokarteEntityMapperTest {

    private final KinokarteEntityMapper mapper = new KinokarteEntityMapper();

    private final KinokarteId kartenId =
            new KinokarteId(UUID.fromString("2b7a2a4d-6d3e-4d0a-9a3f-2b1d5e0c9a11"));
    private final Auftragsnummer auftragsnummer =
            new Auftragsnummer(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));
    private final VorstellungId vorstellungId =
            new VorstellungId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90"));
    private final LocalDateTime beginn = LocalDateTime.parse("2025-03-23T14:30");

    @Test
    void toEntity() {
        // arrange
        var kinokarte = new Kinokarte(kartenId, auftragsnummer, vorstellungId,
                new Film("The Fast and the Curious"), new Beginn(beginn), new Saal("kleiner Saal"),
                new ReiheNummer(4), new PlatzNummer(2));

        // act
        var entity = mapper.toEntity(kinokarte);

        // assert
        assertThat(entity.getKartenId()).isEqualTo(kartenId.wert());
        assertThat(entity.getAuftragsnummer()).isEqualTo(auftragsnummer.nummer());
        assertThat(entity.getVorstellungId()).isEqualTo(vorstellungId.uuid());
        assertThat(entity.getFilmName()).isEqualTo("The Fast and the Curious");
        assertThat(entity.getBeginn()).isEqualTo(beginn);
        assertThat(entity.getSaalName()).isEqualTo("kleiner Saal");
        assertThat(entity.getReihe()).isEqualTo(4);
        assertThat(entity.getPlatz()).isEqualTo(2);
    }

    @Test
    void toDomain() {
        // arrange
        var entity = new KinokarteEntity(kartenId.wert(), auftragsnummer.nummer(), vorstellungId.uuid(),
                "The Fast and the Curious", beginn, "kleiner Saal", 4, 2);

        // act
        var kinokarte = mapper.toDomain(entity);

        // assert
        assertThat(kinokarte.getId()).isEqualTo(kartenId);
        assertThat(kinokarte.getAuftragsnummer()).isEqualTo(auftragsnummer);
        assertThat(kinokarte.getVorstellungId()).isEqualTo(vorstellungId);
        assertThat(kinokarte.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
        assertThat(kinokarte.getBeginn()).isEqualTo(new Beginn(beginn));
        assertThat(kinokarte.getSaal()).isEqualTo(new Saal("kleiner Saal"));
        assertThat(kinokarte.getReihe()).isEqualTo(new ReiheNummer(4));
        assertThat(kinokarte.getPlatz()).isEqualTo(new PlatzNummer(2));
    }
}
