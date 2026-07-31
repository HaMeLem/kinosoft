package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers;

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
import org.springframework.stereotype.Component;

@Component
public class KinokarteEntityMapper {

    public KinokarteEntity toEntity(Kinokarte kinokarte) {
        return new KinokarteEntity(
                kinokarte.getId().wert(),
                kinokarte.getAuftragsnummer().nummer(),
                kinokarte.getVorstellungId().uuid(),
                kinokarte.getFilm().name(),
                kinokarte.getBeginn().zeitpunkt(),
                kinokarte.getSaal().name(),
                kinokarte.getReihe().nummer(),
                kinokarte.getPlatz().nummer()
        );
    }

    public Kinokarte toDomain(KinokarteEntity kinokarteEntity) {
        return new Kinokarte(
                new KinokarteId(kinokarteEntity.getKartenId()),
                new Auftragsnummer(kinokarteEntity.getAuftragsnummer()),
                new VorstellungId(kinokarteEntity.getVorstellungId()),
                new Film(kinokarteEntity.getFilmName()),
                new Beginn(kinokarteEntity.getBeginn()),
                new Saal(kinokarteEntity.getSaalName()),
                new ReiheNummer(kinokarteEntity.getReihe()),
                new PlatzNummer(kinokarteEntity.getPlatz())
        );
    }
}
