package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.repositories;

import de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model.FilmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilmJpaRepository extends JpaRepository<FilmEntity, UUID> {
}
