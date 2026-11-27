package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model;

import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Status;
import jakarta.persistence.*;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "filmvorfuehrungen", schema = "filmvorfuehrung")
@Getter
public class FilmvorfuehrungEntity {
    /**
     * Technischer Primärschlüssel; entspricht der Vorführungs-ID aus der Domäne.
     * Wird extern vergeben (kein {@code @GeneratedValue}) und ist nach dem
     * Anlegen nicht mehr änderbar.
     */
    @Id
    @Column(name = "vorfuehrung_id", nullable = false, updatable = false)
    private UUID vorfuehrungId;

    // geplanter Beginn
    @Column(name = "geplanter_beginn", nullable = false)
    private LocalDateTime geplanterBeginn;

    // tatsaechlicher Beginn
    @Column(name = "tatsaechlicher_beginn")
    private LocalDateTime tatsaechlicherBeginn;

    @Column(name = "saal", nullable = false)
    private int saal;

    // Anzeige des Status GEPLANT, UNTERBROCHEN, LAUFEND,BEENDET .
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    //
    @Column(name = "werbelaufzeit_minuten", nullable = false)
    private long werbelaufzeit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "film_id", nullable = false, updatable = false)
    private FilmEntity film;

    // Eine Filmvorführung kann mehrere Unterbrechungen besitzen.
    // CascadeType.ALL überträgt alle Entity-Operationen (z. B. Persistieren und Löschen)
    // auf die zugehörigen Unterbrechungen.
    // orphanRemoval = true sorgt dafür, dass Unterbrechungen aus der Datenbank
    // gelöscht werden, wenn sie aus der Liste entfernt werden.
    // Die Spalte vorfuehrung_id in der Tabelle der Unterbrechungen
    // stellt die Zuordnung zur jeweiligen Filmvorführung her.
    @OneToMany(
            mappedBy = "filmvorfuehrung",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<UnterbrechungEntity> unterbrechungen = new ArrayList<>();

    // Nur für JPA
    protected FilmvorfuehrungEntity() {
    }

    public FilmvorfuehrungEntity(
            UUID vorfuehrungId,
            LocalDateTime geplanterBeginn,
            LocalDateTime tatsaechlicherBeginn,
            int saal,
            Status status,
            long werbelaufzeit,
            FilmEntity film,
            List<UnterbrechungEntity> unterbrechungen) {
        this.vorfuehrungId = vorfuehrungId;
        this.geplanterBeginn = geplanterBeginn;
        this.tatsaechlicherBeginn = tatsaechlicherBeginn;
        this.saal = saal;
        this.status = status;
        this.werbelaufzeit = werbelaufzeit;
        this.film = film;
        aktualisiereUnterbrechungen(unterbrechungen);
    }

    public void aktualisiereUnterbrechungen(List<UnterbrechungEntity> unterbrechungen) {
        this.unterbrechungen.clear();
        for (UnterbrechungEntity unterbrechung : unterbrechungen) {
            unterbrechung.setFilmvorfuehrung(this);
            this.unterbrechungen.add(unterbrechung);
        }
    }
}