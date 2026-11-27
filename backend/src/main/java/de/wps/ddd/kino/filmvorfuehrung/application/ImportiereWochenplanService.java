package de.wps.ddd.kino.filmvorfuehrung.application;


import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.FilmvorfuehrungRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Film;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Filmlaufzeit;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Filmtitel;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.*;
import de.wps.ddd.kino.wochenplanung.WochenplanDto;
import de.wps.ddd.kino.wochenplanung.Wochenplanauskunft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("filmvorfuehrungImportiereWochenplanService")
@ApplicationService
@RequiredArgsConstructor
public class ImportiereWochenplanService {
    private final Wochenplanauskunft wochenplanauskunft;
    private final FilmvorfuehrungRepository filmvorfuehrungRepository;
    private final FilmRepository filmRepository;

    @Transactional
    public void importiere(int jahr, int kalenderwoche) {
        // Bei der Schnittstelle der Wochenplanung den vollständigen Wochenplan holen
        WochenplanDto wochenplanDto = wochenplanauskunft.holeWochenplan(jahr, kalenderwoche);

        // zuerst Filme über wochenplanDto.vorstellungen() extrahieren und über das FilmRepo abspeichern
        
            List <Film> filme = new ArrayList<>();
               for ( WochenplanDto.FilmDto filmDto : wochenplanDto.filme())
               { 
                filme.add(new Film( new Filmlaufzeit(filmDto.laufzeit()),
                new Filmtitel(filmDto.titel())));
                }
            List<Film> gespeicherteFilme = filmRepository.fuegeFilmeHinzu(filme);

            // Filmtitel wird zu Schlüssel, ID zum Wert
            Map<String, FilmId> filmIdsNachTitel = gespeicherteFilme.stream()
                .collect(Collectors.toMap(film -> film.getFilmtitel().titel(), Film::getFilmId));

        // Alle Filmvorstellungen als eigene fachliche Objekte der Filmvorführung erstellen
        for (WochenplanDto.VorstellungDto vorstellung : wochenplanDto.vorstellungen())
        {
            // passende FilmId setzen
            FilmId filmId = filmIdsNachTitel.get(vorstellung.filmTitel());

            if (filmId == null)
                    {
                        throw new IllegalArgumentException(
                        "Kein importierter Film gefunden: " + vorstellung.filmTitel());
                    }
            // Erstellen des Filmvorführung-Objektes -> tats. Beginn = null, leere Liste an Unterbrechungen
            Filmvorfuehrung filmvorfuehrung = new Filmvorfuehrung(
                    new VorfuehrungId(vorstellung.id()),
                    new GeplanterBeginn(vorstellung.geplanterStart()),
                    null,
                    mapSaal(vorstellung.saalName()),
                    Status.GEPLANT,
                    filmId,
                    new ArrayList<>()//List.of()-> immutable List ergibt das Sinn??
                    );

            // Die Filmvorführung zum Speichern weitergeben.
            filmvorfuehrungRepository.fuegeVorfuehrungHinzu(filmvorfuehrung);
        }
    }
    /*übersetzt den Saalnamen aus dem Event z.B."kleiner Saal" in
  das passende Einlass-Enum Saal.KLEINER_SAAL*/
    private Saal mapSaal(String saalname)
    {
        if (saalname.equals("kleiner Saal")) {
            return Saal.KLEINER_SAAL;
        }
        if (saalname.equals("großer Saal")) {
            return Saal.GROSSER_SAAL;
        }
        throw new IllegalArgumentException("Unbekannter Saalname: " + saalname);
    }
}
