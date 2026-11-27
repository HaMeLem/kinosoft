package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.repositories;

import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model.FilmvorfuehrungEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilmvorfuehrungJpaRepository
        extends JpaRepository<FilmvorfuehrungEntity, UUID> {
}
