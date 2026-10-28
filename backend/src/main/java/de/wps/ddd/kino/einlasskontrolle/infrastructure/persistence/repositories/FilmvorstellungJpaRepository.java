package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.repositories;

import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.FilmvorstellungEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilmvorstellungJpaRepository extends JpaRepository<FilmvorstellungEntity, UUID> {
}
