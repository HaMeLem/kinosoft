package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Entity(name = "Filmvorfuehrung_FilmEntity")
@Table(name = "film", schema = "filmvorfuehrung")
@Getter
public class FilmEntity {

    /**
     * Technischer Primärschlüssel; entspricht der Film-ID aus der Domäne.
     * Wird extern vergeben (kein {@code @GeneratedValue}) und ist nach dem
     * Anlegen nicht mehr änderbar.
     */
    @Id
    @Column(name = "film_id", nullable = false, updatable = false)
    private UUID filmId;

    // Laufzeit des Films in Minuten
    @Column(name = "filmlaufzeit_minuten", nullable = false)
    private long filmlaufzeit;

    // Anzeigetitel des Films.
    @Column(name = "filmtitel", nullable = false)
    private String filmtitel;

    // Nur für JPA
    protected FilmEntity() {
    }

    // Konstruktor zum Erzeugen einer neuen Entity, z. B. durch den Mapper
    public FilmEntity(
            UUID filmId,
            long filmlaufzeit,
            String filmtitel
    ) {
        this.filmId = filmId;
        this.filmlaufzeit = filmlaufzeit;
        this.filmtitel = filmtitel;
    }
}