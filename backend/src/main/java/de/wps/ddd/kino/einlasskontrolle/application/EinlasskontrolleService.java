package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.einlasskontrolle.domain.Ablehnungsgrund;
import de.wps.ddd.kino.einlasskontrolle.domain.Einlassentscheidung;
import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.FilmvorstellungenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;


@ApplicationService
@Service
@Transactional
public class EinlasskontrolleService {

    private final KinokartenRepository kinokartenRepository;
    private final FilmvorstellungenRepository filmvorstellungenRepository;
    private final Clock clock;

    public EinlasskontrolleService(
            KinokartenRepository kinokartenRepository,
            FilmvorstellungenRepository filmvorstellungenRepository,
            Clock clock
    ) {
        this.kinokartenRepository = kinokartenRepository;
        this.filmvorstellungenRepository = filmvorstellungenRepository;
        this.clock = clock;
    }

    public Kinokarte kinokarteValidierenUndEntwerten(KinokartenId kinokartenId) {

        // Kinokarte suchen
        Kinokarte kinokarte = kinokartenRepository.hole(kinokartenId)
                .orElseThrow(() -> new IllegalStateException("Kinokarte wurde nicht gefunden."));
        //      if (gefunden.isEmpty()) {
        //    return new Einlassentscheidung.Abgelehnt(
        //            Ablehnungsgrund.UNGUELTIGER_CODE);
        //      }

        // Vorstellung der Kinokarte ermitteln
        VorstellungsId vorstellungsId = kinokarte.getFilmvorstellung();

        // Einlass prüfen
        Einlassentscheidung entscheidung = pruefeEinlass(kinokartenId, vorstellungsId);

        if (entscheidung instanceof Einlassentscheidung.Abgelehnt abgelehnt) {
            throw new IllegalStateException(abgelehnt.grund().meldung());
            // return new Einlassentscheidung.Abgelehnt(abgelehnt.grund());
        }

        // Entwertete Kinokarte zurückgeben
        return kinokartenRepository.hole(kinokartenId).orElseThrow();
    }

    public Einlassentscheidung pruefeEinlass(KinokartenId kinokartenId, VorstellungsId vorstellungsId) {

        // UNGUELTIGER_CODE: Es gibt keine Karte zu diesem Code
        Optional<Kinokarte> gefunden = kinokartenRepository.hole(kinokartenId);
        if (gefunden.isEmpty()) {
            return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.UNGUELTIGER_CODE);
        }
        Kinokarte kinokarte = gefunden.get();

        // Die Filmvorstellung, an der der Gast gerade einlassen will
        Filmvorstellung filmvorstellung = filmvorstellungenRepository.hole(vorstellungsId)
                .orElseThrow(() -> new IllegalStateException("Vorstellung wurde nicht gefunden."));
        //                 new RessourceNichtGefunden("Vorstellung wurde nicht gefunden."));

        // FALSCHE_VORSTELLUNG, BEREITS_ENTWERTET und ZU_FRUEH entscheidet die Domain
        Einlassentscheidung entscheidung =
                kinokarte.entwerten(filmvorstellung, LocalDateTime.now(clock));

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