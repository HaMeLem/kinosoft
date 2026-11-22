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
    private final GeplantesEnde geplantesEnde;
    private TatsaechlicherBeginn tatsaechlicherBeginn;
    private Phase phase;
    private Saal saal;
    private Status status;
    private final Werbelaufzeit werbelaufzeit = new Werbelaufzeit(900); // 900 = 15 Minuten in Sekunden
    private final FilmId film;
    private List<Unterbrechung> unterbrechungen;
    private LocalDateTime jetzt;

    public Filmvorfuehrung(
        VorfuehrungId id,
        GeplanterBeginn geplanterBeginn,
        GeplantesEnde geplantesEnde,
        TatsaechlicherBeginn tatsaechlicherBeginn,
        Saal saal,
        Status status,
        FilmId film,
        List<Unterbrechung> unterbrechungen){
            this.id = id;
            this.geplanterBeginn = geplanterBeginn;
            this.geplantesEnde = geplantesEnde;
            this.tatsaechlicherBeginn = tatsaechlicherBeginn;
            this.phase = istInPhase();
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
            this.geplantesEnde = null;
            this.tatsaechlicherBeginn = tatsaechlicherBeginn;
            this.phase = istInPhase();
            this.saal = saal;
            this.status = status;
            this.film = film;
            this.unterbrechungen = unterbrechungen;
    } 

    /* Dauer in Sekunden, sie die Filmvorführung während der Werbung insgesamt unterbrochen war.
     * Es ist nur die Zeit während der Werbung relevant, weil sich nur dann Unterbrechungszeiten auf die
     * Vorführungsphase (Werbung oder Film) auswirken. */
    private long unterbrechungszeitWaehrendWerbung(){
        long alleUnterbrechungssekundenSummiert = 0;
        long dieseUnterbrechungslaufzeit;
        // Falls es Unterbrechungen gibt
        if(!(unterbrechungen.isEmpty())){
            for(Unterbrechung unterbrechung : unterbrechungen){
                LocalDateTime unterbrechungsbeginn = unterbrechung.getUnterbrechungsbeginn().zeitpunkt();
                LocalDateTime unterbrechungsende = unterbrechung.getUnterbrechungsende().zeitpunkt();
                LocalDateTime werbeende = (this.tatsaechlicherBeginn.zeitpunkt()).plusSeconds(alleUnterbrechungssekundenSummiert);
                // Prüft, ob die Unterbrechung während der Werbung ist
                if(unterbrechungsbeginn.isBefore(werbeende)){
                    dieseUnterbrechungslaufzeit = unterbrechungsbeginn.until(unterbrechungsende, ChronoUnit.SECONDS);
                    alleUnterbrechungssekundenSummiert = alleUnterbrechungssekundenSummiert + dieseUnterbrechungslaufzeit;
                }
            }
        }
        return alleUnterbrechungssekundenSummiert;
    }

    // Gibt die Phase (Werbung oder Film) zurück, in der sich die Filmvorführung befindet
    private Phase istInPhase(){
        this.jetzt = LocalDateTime.now();
        LocalDateTime werbeende = (this.tatsaechlicherBeginn.zeitpunkt()).plusSeconds(werbelaufzeit.sekunden()+unterbrechungszeitWaehrendWerbung());
        if(jetzt.isBefore(werbeende)) return Phase.WERBUNG;
        else return Phase.FILM;
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