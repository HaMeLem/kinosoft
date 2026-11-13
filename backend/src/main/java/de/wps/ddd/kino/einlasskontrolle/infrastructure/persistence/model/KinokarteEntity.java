package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Einlasskontrolle_KinokarteEntity")
@Table(name = "kinokarten", schema = "einlasskontrolle")
@Getter
public class KinokarteEntity {
    @Id
    @Column(name = "kinokarten_id", nullable = false, updatable = false)
    private UUID kinokartenId;

    @Column(name = "vorstellungs_id", nullable = false)
    private UUID vorstellungsId;

    @Column(name = "saal", nullable = false)
    private int saal;

    @Column(name = "reihe", nullable = false)
    private int reihe;

    @Column(name = "sitzplatz", nullable = false)
    private int sitzplatz;

    @Column(name = "entwertungszeitpunkt")
    private LocalDateTime entwertungszeitpunkt;


    protected KinokarteEntity() {
        // Nur für JPA
    }

    public KinokarteEntity(
            UUID kinokartenId,
            UUID vorstellungsId,
            int saal,
            int reihe,
            int sitzplatz,
            LocalDateTime entwertungszeitpunkt) {
        this.kinokartenId = kinokartenId;
        this.vorstellungsId = vorstellungsId;
        this.saal = saal;
        this.reihe = reihe;
        this.sitzplatz = sitzplatz;
        this.entwertungszeitpunkt = entwertungszeitpunkt;
    }
}
