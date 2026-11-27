package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence;

import de.wps.ddd.kino.filmvorfuehrung.domain.film.Film;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmRepository;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.mappers.FilmEntityMapper;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.repositories.FilmJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilmRepositoryImpl implements FilmRepository
{
    private final FilmJpaRepository repository;
    private final FilmEntityMapper mapper;

    @Override
    public Optional<Film> hole(FilmId id) {
        return repository.findById(id.uuid())
                .map(mapper::toDomain);
    }

    @Override
    public void speichere(Film film) {
        repository.save(mapper.toEntity(film));
    }
}
