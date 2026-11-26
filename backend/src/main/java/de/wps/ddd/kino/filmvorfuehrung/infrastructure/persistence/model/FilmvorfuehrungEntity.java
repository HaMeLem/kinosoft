package de.wps.ddd.kino.filmvorfuehrung.infrastructure.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "filmvorfuehrung", schema = "filmvorfuehrung")
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
    @Column(name = "geplanter_beginn")
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

    // Wollen wir die Werbung in der Datenbank speichern oder nicht,
    // eigentlich gibt es nichts zu speichern und wir könnten es löschen, bin mir nicht sicher was wir machen wollen?
    @Column(name = "werbelaufzeit_minuten", nullable = false)
    private long werbelaufzeit;

    @Column(name = "film_id", nullable = false, updatable = false)
    private UUID filmId;

    // Eine Filmvorführung kann mehrere Unterbrechungen besitzen.
    // CascadeType.ALL überträgt alle Entity-Operationen (z. B. Persistieren und Löschen)
    // auf die zugehörigen Unterbrechungen.
    // orphanRemoval = true sorgt dafür, dass Unterbrechungen aus der Datenbank
    // gelöscht werden, wenn sie aus der Liste entfernt werden.
    // Die Spalte vorfuehrung_id in der Tabelle der Unterbrechungen
    // stellt die Zuordnung zur jeweiligen Filmvorführung her.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vorfuehrung_id", nullable = false)
    private List<UnterbrechungEntity> unterbrechungen = new ArrayList<>();

    // Nur für JPA
    protected FilmvorfuehrungEntity() {
    }

    /* Warum sind die nicht mit aufgelistet?
    @Identity
    private Phase phase;
    private LocalDateTime jetzt;
    */

    public FilmvorfuehrungEntity(
            UUID vorfuehrungId,
            LocalDateTime geplanterBeginn,
            LocalDateTime tatsaechlicherBeginn,
            int saal,
            Status status,
            long werbelaufzeit,
            UUID filmId,
            List<UnterbrechungEntity> unterbrechungen) {
        this.vorfuehrungId = vorfuehrungId;
        this.geplanterBeginn = geplanterBeginn;
        this.tatsaechlicherBeginn = tatsaechlicherBeginn;
        this.saal = saal;
        this.status = status;
        this.werbelaufzeit = werbelaufzeit;
        this.filmId = filmId;
        this.unterbrechungen = unterbrechungen;
    }
}