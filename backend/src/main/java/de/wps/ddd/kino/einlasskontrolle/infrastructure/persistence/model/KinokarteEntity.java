package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kinokarten", schema = "einlasskontrolle")
@Getter
public class KinokarteEntity {
    @Id
    @Column(name = "kinokarten_id", nullable = false, updatable = false)
    private UUID kinokartenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vorstellungs_id", nullable = false)
    private FilmvorstellungEntity filmvorstellung;

    @Column(name = "saal", nullable = false)
    private int saal;

    @Column(name = "reihe", nullable = false)
    private int reihe;

    @Column(name = "sitzplatz", nullable = false)
    private int sitzplatz;

    @Column(name = "entwertungszeitpunkt")
    private LocalDateTime entwertungszeitpunkt;
}
