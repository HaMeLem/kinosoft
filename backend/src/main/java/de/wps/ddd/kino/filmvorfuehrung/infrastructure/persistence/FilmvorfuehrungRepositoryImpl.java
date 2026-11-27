package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence;

import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Filmvorfuehrung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.FilmvorfuehrungRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.mappers.FilmvorfuehrungEntityMapper;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.repositories.FilmJpaRepository;
import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.repositories.FilmvorfuehrungJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilmvorfuehrungRepositoryImpl implements FilmvorfuehrungRepository {
    private final FilmvorfuehrungJpaRepository repository;
    private final FilmJpaRepository filmRepository;
    private final FilmvorfuehrungEntityMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<Filmvorfuehrung> hole(VorfuehrungId id) {
        return repository.findById(id.uuid())
                .map(mapper::toDomain);
    }

    @Transactional
    @Override
    public void speichere(Filmvorfuehrung filmvorfuehrung) {
        var filmId = filmvorfuehrung.getFilm().uuid();
        var film = filmRepository.findById(filmId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Film nicht gefunden: " + filmId));

        repository.save(mapper.toEntity(filmvorfuehrung, film));
    }
}
