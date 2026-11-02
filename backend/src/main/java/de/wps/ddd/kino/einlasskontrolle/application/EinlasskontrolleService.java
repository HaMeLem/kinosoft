package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.einlasskontrolle.domain.Ablehnungsgrund;
import de.wps.ddd.kino.einlasskontrolle.domain.Einlassentscheidung;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import de.wps.ddd.kino.filmauswahl.data.Vorstellung;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class EinlasskontrolleService {

    private final KinokartenRepository kinokartenRepository;
    private final VorstellungenRepository vorstellungenRepository;
    private final Clock clock;

    public EinlasskontrolleService(
            KinokartenRepository kinokartenRepository,
            VorstellungenRepository vorstellungenRepository,
            Clock clock
    ) {
        this.kinokartenRepository = kinokartenRepository;
        this.vorstellungenRepository = vorstellungenRepository;
        this.clock = clock;
    }

    public Einlassentscheidung pruefeEinlass(KinokartenId kinokartenId, UUID vorstellungsUuid) {

        // UNGUELTIGER_CODE: Es gibt keine Karte zu diesem Code
        Optional<Kinokarte> gefunden = kinokartenRepository.finde(kinokartenId);
        if (gefunden.isEmpty()) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.UNGUELTIGER_CODE);
        }
        Kinokarte kinokarte = gefunden.get();

        // Die Vorstellung, an der der Gast gerade einlassen will
        Vorstellung vorstellung = vorstellungenRepository.hole(vorstellungsUuid);
        if (vorstellung == null) {
            throw new IllegalStateException("Vorstellung wurde nicht gefunden.");
        }

        // FALSCHE_VORSTELLUNG, BEREITS_ENTWERTET und ZU_FRUEH entscheidet die Domain
        Einlassentscheidung entscheidung =
                kinokarte.entwerten(vorstellung, LocalDateTime.now(clock));

        // Nur speichern, wenn die Karte wirklich entwertet wurde
        if (entscheidung instanceof Einlassentscheidung.Erlaubt) {
            kinokartenRepository.speichere(kinokarte);
        }

        return entscheidung;
    }

    // Meldung für die Anzeige am Einlass 
    public String meldungFuer(Einlassentscheidung entscheidung) {
        if (entscheidung instanceof Einlassentscheidung.Erlaubt erlaubt) {
            return "Einlass erlaubt: Saal " + erlaubt.saal()
                    + ", Reihe " + erlaubt.reihe()
                    + ", Platz " + erlaubt.sitzplatz();
        }
        Einlassentscheidung.Abgelehnt abgelehnt = (Einlassentscheidung.Abgelehnt) entscheidung;
        return abgelehnt.grund().meldung();
    }
}