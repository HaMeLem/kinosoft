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
        var entity = new FilmvorstellungEntity();
        entity.setVorstellungsId(filmvorstellung.getId().id());
        entity.setStart(filmvorstellung.getStart().start());
        entity.setEnde(filmvorstellung.getEnde().ende());
        entity.setEinlassVorlaufMinuten(filmvorstellung.getEinlassVorlauf().wert());
        return entity;
    }

    public Filmvorstellung toDomain(FilmvorstellungEntity entity) {
        var filmvorstellung = new Filmvorstellung(
                new VorstellungsId(entity.getVorstellungsId()),
                new GeplanteStartzeit(entity.getStart()),
                new GeplanteEndzeit(entity.getEnde())
        );
        return filmvorstellung;
    }
}
