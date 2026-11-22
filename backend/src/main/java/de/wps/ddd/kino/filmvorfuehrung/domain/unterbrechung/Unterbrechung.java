package de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung;

import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity 
@Getter
public class Unterbrechung {
    @Identity private final UnterbrechungId unterbrechungId;
    private final Unterbrechungsbeginn unterbrechungsbeginn;
    private final Unterbrechungsende unterbrechungsende;

    public Unterbrechung(
        UnterbrechungId unterbrechungId,
        Unterbrechungsbeginn unterbrechungsbeginn,
        Unterbrechungsende unterbrechungsende) {
            this.unterbrechungId = unterbrechungId;
            this.unterbrechungsbeginn = unterbrechungsbeginn;
            this.unterbrechungsende = unterbrechungsende;
    }

    // Unterbrechungsbeginn
    public Unterbrechung(
        UnterbrechungId unterbrechungId,
        Unterbrechungsbeginn unterbrechungsbeginn) {
            this.unterbrechungId = unterbrechungId;
            this.unterbrechungsbeginn = unterbrechungsbeginn;
            this.unterbrechungsende = null;
    }
}