package de.wps.ddd.kino.einlasskontrolle.domain;

import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;

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

        // Regel 1: Karte wurde schon benutzt
        if (entwertungszeitpunkt != null) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.BEREITS_ENTWERTET);
        }

        // Regel 2: Einlass ist noch nicht offen (erst 30 Minuten vor Beginn)
        if (filmvorstellung.istEinlassNochNichtOffen(jetzt)) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.ZU_FRUEH);
        }

        // Regel 3: Vorstellung ist schon vorbei.
        // Maßgeblich ist das geplante Ende. Das tatsächliche Ende ist erst bekannt,
        // wenn der Film vorbei ist, und damit für die Einlasskontrolle zu spät.
        if (filmvorstellung.istEinlassVorbei(jetzt)) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.ZU_SPAET);
        }

        // Alles in Ordnung: Karte entwerten und Platz mitteilen
        this.entwertungszeitpunkt = new Entwertungszeitpunkt(jetzt);
        return new Einlassentscheidung.Erlaubt(saal, reihe, sitzplatz);
    }
}
