package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers;

import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.GeplanteEndzeit;
import de.wps.ddd.kino.einlasskontrolle.domain.GeplanteStartzeit;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.FilmvorstellungEntity;
import org.springframework.stereotype.Component;

@Component
public class FilmvorstellungEntityMapper {

    public FilmvorstellungEntity toEntity(Filmvorstellung filmvorstellung) {
        return new FilmvorstellungEntity(
                filmvorstellung.getId().id(),
                filmvorstellung.getStart().start(),
                filmvorstellung.getEnde().ende()
        );
    }

    public Filmvorstellung toDomain(FilmvorstellungEntity entity) {
        return new Filmvorstellung(
                new VorstellungsId(entity.getVorstellungsId()),
                new GeplanteStartzeit(entity.getGeplanterStart()),
                new GeplanteEndzeit(entity.getGeplantesEnde())
        );
    }
}
