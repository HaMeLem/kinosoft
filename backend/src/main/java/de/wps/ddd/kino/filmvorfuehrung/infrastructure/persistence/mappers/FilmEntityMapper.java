package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.mappers;

import de.wps.ddd.kino.filmvorfuehrung.domain.film.Film;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Filmlaufzeit;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Filmtitel;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model.FilmEntity;
import org.springframework.stereotype.Component;

@Component
public class FilmEntityMapper {

    public FilmEntity toEntity(Film film) {
        return new FilmEntity(
                film.getFilmId().uuid(),
                film.getFilmlaufzeit().minuten(),
                film.getFilmtitel().titel());
    }

    public Film toDomain(FilmEntity entity) {
        return new Film(
                new FilmId(entity.getFilmId()),
                new Filmlaufzeit(entity.getFilmlaufzeit()),
                new Filmtitel(entity.getFilmtitel()));
    }
}
