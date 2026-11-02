package de.wps.ddd.kino.einlasskontrolle.domain;

import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import de.wps.ddd.kino.filmauswahl.data.Vorstellung;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    // Baut eine bereits existierende Kinokarte aus ihrem gespeicherten Zustand wieder auf.
    // Diese Methode ist ausschließlich für die Persistenzschicht gedacht (z. B. das Repository, das eine Karte aus der Datenbank lädt). Sie ist nötig, weil der normale
    // Konstruktor eine Karte immer als "noch nicht entwertet" anlegt. Eine Karte, die
    // bereits benutzt wurde, könnte man damit nicht wiederherstellen, und die Regel
    // BEREITS_ENTWERTET würde nie greifen.
    // Nur für die Rekonstruktion aus der Persistenz (z. B. aus der Datenbank)
    public static Kinokarte rekonstruiere(KinokartenId id, Reihe reihe, Sitzplatz sitzplatz,
                                          Saal saal, VorstellungsId vorstellung,
                                          Entwertungszeitpunkt entwertungszeitpunkt) {
        Kinokarte karte = new Kinokarte(id, reihe, sitzplatz, saal, vorstellung);
        karte.entwertungszeitpunkt = entwertungszeitpunkt;
        return karte;
    }

    public Einlassentscheidung entwerten(Vorstellung vorstellung, LocalDateTime jetzt)
    {
    // Regel 1: Karte gehört zu einer anderen Vorstellung
    if (!gehoertZu(vorstellung)) {
        return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.FALSCHE_VORSTELLUNG);
    }

    // Regel 2: Karte wurde schon benutzt
    if (entwertungszeitpunkt != null) {
        return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.BEREITS_ENTWERTET);
    }

    // Regel 3: Einlass ist noch nicht offen (30 Minuten vor Beginn)
    if (jetzt.isBefore(vorstellung.getBeginn().minus(
            EinlassVorlauf.EINLASS_VORLAUF_MINUTEN.wert(), ChronoUnit.MINUTES))) {
        return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.ZU_FRUEH);
    }

    // Alles in Ordnung: Karte entwerten und Platz mitteilen
    this.entwertungszeitpunkt = new Entwertungszeitpunkt(jetzt);
        return new Einlassentscheidung.Erlaubt(saal, reihe, sitzplatz);
    }

    /**
    * Prüft, ob die Karte zur übergebenen Vorstellung gehört.
    *
    * Verglichen wird über die fachliche UUID (getUuid), nicht über die technische
    * Datenbank-ID (getId, Long) der Vorstellung.
    */
    private boolean gehoertZu(Vorstellung vorstellung)
    {
        return filmvorstellung.id().equals(vorstellung.getUuid());
    }
}
