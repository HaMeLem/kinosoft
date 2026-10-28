package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.repositories;

import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model.KinokarteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KinokarteJpaRepository extends JpaRepository<KinokarteEntity, UUID> {
}
