package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model.VorstellungEntity;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungKategorie;
import org.springframework.stereotype.Component;

@Component
public class VorstellungEntityMapper {

    public Vorstellung toDomain(VorstellungEntity entity) {
        return new Vorstellung(
                new VorstellungId(entity.getUuid()),
                new Saal(entity.getSaal()),
                new Beginn(entity.getBeginn()),
                new Film(entity.getFilm()),
                VorstellungKategorie.valueOf(entity.getKategorie())
        );
    }

    public VorstellungEntity toEntity(Vorstellung vorstellung) {
        return new VorstellungEntity(
                vorstellung.getId().uuid(),
                vorstellung.getBeginn().zeitpunkt(),
                vorstellung.getSaal().name(),
                vorstellung.getFilm().name(),
                vorstellung.getKategorie().name()
        );
    }
}
