package de.wps.ddd.kino.einlasskontrolle.domain;

import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@AggregateRoot
@Getter
public class Kinokarte {
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
            VorstellungsId filmvorstellung) {
        this.id = id;
        this.reihe = reihe;
        this.sitzplatz = sitzplatz;
        this.saal = saal;
        this.filmvorstellung = filmvorstellung;
        this.entwertungszeitpunkt = null;
    }

    // Baut eine bereits gespeicherte Karte wieder auf (nur für das Repository
    // gedacht).
    // Nötig, weil der Konstruktor eine Karte immer als "nicht entwertet" anlegt.
    public static Kinokarte rekonstruiere(KinokartenId id, Reihe reihe, Sitzplatz sitzplatz,
            Saal saal, VorstellungsId vorstellung,
            Entwertungszeitpunkt entwertungszeitpunkt) {
        Kinokarte karte = new Kinokarte(id, reihe, sitzplatz, saal, vorstellung);
        karte.entwertungszeitpunkt = entwertungszeitpunkt;
        return karte;
    }

    public Einlassentscheidung entwerten(Filmvorstellung filmvorstellung, LocalDateTime jetzt) {
        // Regel 1: Karte gehört zu einer anderen Vorstellung
        if (!gehoertZu(filmvorstellung)) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.FALSCHE_VORSTELLUNG);
        }

        // Regel 2: Karte wurde schon benutzt
        if (entwertungszeitpunkt != null) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.BEREITS_ENTWERTET);
        }

        // Regel 3: Einlass ist noch nicht offen (30 Minuten vor Beginn)
        if (jetzt.isBefore(filmvorstellung.getStart().start().minus(
                filmvorstellung.getEinlassVorlauf().wert(), ChronoUnit.MINUTES))) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.ZU_FRUEH);
        }

        // Alles in Ordnung: Karte entwerten und Platz mitteilen
        this.entwertungszeitpunkt = new Entwertungszeitpunkt(jetzt);
        return new Einlassentscheidung.Erlaubt(saal, reihe, sitzplatz);
    }

    /**
     * Prüft, ob die Karte zur übergebenen Filmvorstellung gehört.
     *
     * Verglichen wird über die VorstellungsId.
     */
    private boolean gehoertZu(Filmvorstellung filmvorstellung) {
        return this.filmvorstellung.equals(filmvorstellung.getId());
    }
}
