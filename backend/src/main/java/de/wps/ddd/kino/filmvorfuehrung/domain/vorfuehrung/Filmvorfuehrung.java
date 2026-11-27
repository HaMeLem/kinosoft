package de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung;

import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
@Entity  
@Getter
public class Filmvorfuehrung {

    @Identity private final VorfuehrungId id;
    private final GeplanterBeginn geplanterBeginn;
    private TatsaechlicherBeginn tatsaechlicherBeginn;
    private Saal saal;
    private Status status;
    private final Werbelaufzeit werbelaufzeit = new Werbelaufzeit(15); // Die Werbung dauert immmer 15 Minuten
    private final FilmId film;
    private List<Unterbrechung> unterbrechungen;
    private LocalDateTime jetzt;

    public Filmvorfuehrung(
        VorfuehrungId id,
        GeplanterBeginn geplanterBeginn,
        TatsaechlicherBeginn tatsaechlicherBeginn,
        Saal saal,
        Status status,
        FilmId film,
        List<Unterbrechung> unterbrechungen){
            this.id = id;
            this.geplanterBeginn = geplanterBeginn;
            this.tatsaechlicherBeginn = tatsaechlicherBeginn;
            this.saal = saal;
            this.status = status;
            this.film = film;
            this.unterbrechungen = unterbrechungen;
    } 
    
    public Filmvorfuehrung(
        VorfuehrungId id,
        TatsaechlicherBeginn tatsaechlicherBeginn,
        Saal saal,
        Status status,
        FilmId film,
        List<Unterbrechung> unterbrechungen){
            this.id = id;
            this.geplanterBeginn = null;
            this.tatsaechlicherBeginn = tatsaechlicherBeginn;
            this.saal = saal;
            this.status = status;
            this.film = film;
            this.unterbrechungen = unterbrechungen;
    } 

    // Fragt Status der Filmvorführung ab
    public boolean istGeplant() {
        return this.status == Status.GEPLANT;
    }
    public boolean istUnterbrochen() {
        return this.status == Status.UNTERBROCHEN;
    }
    public boolean istLaufend() {
        return this.status == Status.LAUFEND;
    }
    public boolean istBeendet() {
        return this.status == Status.BEENDET;
    }
}