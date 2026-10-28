package de.wps.ddd.kino.einlasskontrolle.domain;

import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;

@AggregateRoot
@Getter
public class Kinokarte
{
    @Identity
    private final KinokartenId id;
    private final Reihe reihe;
    private final Sitzplatz sitzplatz;
    private final Saal saal;
    private final VorstellungsId filmvorstellung;
    private Entwertungszeitpunkt entwertungszeitpunkt;


    public Kinokarte(
            KinokartenId id,
            Reihe reihe,
            Sitzplatz sitzplatz,
            Saal saal,
            VorstellungsId filmvorstellung
    )
    {
        this.id = id;
        this.reihe = reihe;
        this.sitzplatz = sitzplatz;
        this.saal = saal;
        this.filmvorstellung = filmvorstellung;
        this.entwertungszeitpunkt = null;
    }

    public void entwerten() {
        this.entwertungszeitpunkt = new Entwertungszeitpunkt(LocalDateTime.now());
    }

}
