package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "unterbrechung", schema = "filmvorfuehrung")
@Getter
public class UnterbrechungEntity {

    /** Technischer Primärschlüssel; entspricht der Unterbrechungs-ID aus der Domäne.
     * Wird extern vergeben (kein {@code @GeneratedValue}) und ist nach dem
     * Anlegen nicht mehr änderbar.
     */
    @Id
    @Column(name = "unterbrechung_id", nullable = false, updatable = false)
    private UUID unterbrechungId;

    @Column(name = "unterbrechungs_beginn", nullable = false)
    private LocalDateTime unterbrechungsbeginn;

    @Column(name = "unterbrechungs_ende", nullable = true)
    private LocalDateTime unterbrechungsende;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vorfuehrung_id", nullable = false)
    private FilmvorfuehrungEntity filmvorfuehrung;

    // Nur für JPA
    protected UnterbrechungEntity() {
    }

    // Für das erstmalige Anlegen aus dem Domain-Modell
    public UnterbrechungEntity(
            UUID unterbrechungId,
            LocalDateTime unterbrechungsbeginn,
            LocalDateTime unterbrechungsende
    ) {
        this.unterbrechungId = unterbrechungId;
        this.unterbrechungsbeginn = unterbrechungsbeginn;
        this.unterbrechungsende = unterbrechungsende;
    }

    void setFilmvorfuehrung(FilmvorfuehrungEntity filmvorfuehrung) {
        this.filmvorfuehrung = filmvorfuehrung;
    }
}
