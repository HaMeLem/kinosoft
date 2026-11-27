package de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung;

import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity 
@Getter
public class Unterbrechung {
    @Identity private final UnterbrechungId unterbrechungId;
    private final Unterbrechungsbeginn unterbrechungsbeginn;
    private Unterbrechungsende unterbrechungsende;

    // Beginnende Unterbrechung speichern
    public Unterbrechung(
        UnterbrechungId unterbrechungId,
        Unterbrechungsbeginn unterbrechungsbeginn) {
            this.unterbrechungId = unterbrechungId;
            this.unterbrechungsbeginn = unterbrechungsbeginn;
            this.unterbrechungsende = null;
    }
    
    // Beendete Unterbrechung speichern
    public void unterbrechungsendeSpeichern(
        Unterbrechungsende unterbrechungsende) {
            this.unterbrechungsende = unterbrechungsende;
    }
}