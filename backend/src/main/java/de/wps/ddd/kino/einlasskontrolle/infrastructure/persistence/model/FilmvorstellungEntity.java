package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vorstellungen", schema = "einlasskontrolle")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FilmvorstellungEntity {

    @Id
    private UUID vorstellungsId;

    @Column(name = "saal", nullable = false)
    private LocalDateTime geplanterStart;

    @Column(name = "ende", nullable = false)
    private LocalDateTime geplantesEnde;
}
