package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vorstellungen", schema = "einlasskontrolle")
@Getter
public class FilmvorstellungEntity {

    @Id
    @Column(name = "vorstellungs_id", nullable = false, updatable = false)
    private UUID vorstellungsId;

    @Column(name = "geplanter_start", nullable = false)
    private LocalDateTime geplanterStart;

    @Column(name = "geplantes_ende", nullable = false)
    private LocalDateTime geplantesEnde;

    // Nur für JPA
    protected FilmvorstellungEntity() {
    }

    // Für das erstmalige Anlegen aus dem Domain-Modell
    public FilmvorstellungEntity(
            UUID vorstellungsId,
            LocalDateTime geplanterStart,
            LocalDateTime geplantesEnde
    ) {
        this.vorstellungsId = vorstellungsId;
        this.geplanterStart = geplanterStart;
        this.geplantesEnde = geplantesEnde;
    }
}
