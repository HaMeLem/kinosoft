package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.einlasskontrolle.domain.Ablehnungsgrund;
import de.wps.ddd.kino.einlasskontrolle.domain.Einlassentscheidung;
import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.FilmvorstellungenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;


@ApplicationService
@Service
public class EinlasskontrolleService {

    private final KinokartenRepository kinokartenRepository;
    private final FilmvorstellungenRepository filmvorstellungenRepository;
    private final Clock clock;                                                // Wird als Bean aus der ApplicationConfig injiziert

    public EinlasskontrolleService(
            KinokartenRepository kinokartenRepository,
            FilmvorstellungenRepository filmvorstellungenRepository,
            Clock clock
    ) 
    {   
        this.kinokartenRepository = kinokartenRepository;
        this.filmvorstellungenRepository = filmvorstellungenRepository;
        this.clock = clock;
    }

    @Transactional
    public Einlassentscheidung kinokarteValidierenUndEntwerten(
        KinokartenId kinokartenId)
    {
    
    // Aktuelle Zeit über die injizierte Clock (testbar)
    LocalDateTime jetzt = LocalDateTime.now(clock);

    // Karte laden. Gibt es sie nicht, ist das eine fachliche Ablehnung und kein Fehler.
    Optional<Kinokarte> gefunden = kinokartenRepository.hole(kinokartenId);
    if (gefunden.isEmpty()) {
        return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.ID_UNGUELTIG);
    }
    Kinokarte kinokarte = gefunden.get();

    // Vorstellung laden, an der der Gast einlassen will (aus der Karte).
    Optional<Filmvorstellung> filmvorstellungOptional = filmvorstellungenRepository.hole(kinokarte.getFilmvorstellung());
    if(filmvorstellungOptional.isEmpty()) {
        return new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.ID_UNGUELTIG);
    }

    Filmvorstellung filmvorstellung = filmvorstellungOptional.get();

    // Die Domain entscheidet und entwertet die Karte, wenn der Einlass erlaubt ist
    Einlassentscheidung entscheidung = kinokarte.entwerten(filmvorstellung, jetzt);

    // Nur speichern, wenn die Karte wirklich entwertet wurde
    if (entscheidung instanceof Einlassentscheidung.Erlaubt) {
        kinokartenRepository.speichere(kinokarte);
    }

    return entscheidung;
    }
  
}