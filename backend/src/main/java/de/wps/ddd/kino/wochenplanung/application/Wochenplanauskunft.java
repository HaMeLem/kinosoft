package de.wps.ddd.kino.wochenplanung.application;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.wochenplanung.WochenplanDto;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Film;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmkatalog;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmtitel;
import de.wps.ddd.kino.wochenplanung.wochenplan.Vorstellung;
import de.wps.ddd.kino.wochenplanung.wochenplan.Kalenderwoche;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplaene;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Beantwortet Abfragen auf den (in der Fixture erstellten) Wochenplan und liefert die neutrale,
 * für beide Consumer gemeinsame {@link WochenplanDto}-Sicht (ohne planungsinterne Details). Das
 * Erstellen des Wochenplans erfolgt nicht hier, sondern direkt in der Fixture.
 */
@Service
@ApplicationService
@RequiredArgsConstructor
public class Wochenplanauskunft implements de.wps.ddd.kino.wochenplanung.Wochenplanauskunft {

    private final Filmkatalog filmkatalog;
    private final Wochenplaene wochenplaene;

    @Override
    public WochenplanDto holeWochenplan(int jahr, int kalenderwoche) {
        var wochenplan = findeWochenplan(jahr, kalenderwoche);

        var filme = wochenplan.getVorstellungen().stream()
                .map(Vorstellung::film)
                .distinct()
                .map(this::holeFilm)
                .map(film -> new WochenplanDto.FilmDto(film.titel().wert(), film.laufzeit(), film.posterUrl(),
                        film.fsk().wert(), film.beschreibung(), film.genre(), film.hauptdarsteller(),
                        film.regie(), film.sprache()))
                .toList();

        var vorstellungen = wochenplan.getVorstellungen().stream()
                .map(vorstellung -> new WochenplanDto.VorstellungDto(vorstellung.id().wert(),
                        vorstellung.film().wert(), vorstellung.saal().wert(),
                        vorstellung.kategorie().name(), vorstellung.zeiten().vorstellungsbeginn()))
                .toList();

        return new WochenplanDto(filme, vorstellungen);
    }

    private Film holeFilm(Filmtitel titel) {
        return oderNichtGefunden(filmkatalog.finde(titel), "Film nicht im Katalog: " + titel.wert());
    }

    private Wochenplan findeWochenplan(int jahr, int kalenderwoche) {
        return oderNichtGefunden(wochenplaene.finde(new Kalenderwoche(jahr, kalenderwoche)),
                "Kein Wochenplan für KW " + kalenderwoche + "/" + jahr);
    }

    private <T> T oderNichtGefunden(Optional<T> gefunden, String meldung) {
        RessourceNichtGefunden.wenn(gefunden.isEmpty(), meldung);
        return gefunden.get();
    }
}
