package de.wps.ddd.kino.filmvorfuehrung.application;


import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmvorfuehrung.domain.FilmRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.FilmvorfuehrungRepository;
import de.wps.ddd.kino.wochenplanung.WochenplanDto;
import de.wps.ddd.kino.wochenplanung.Wochenplanauskunft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
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

        //1. zuerst Filme über wochenplanDto.vorstellungen() extrahieren und über das FilmRepo abspeichern
        //mit for Schleife durchlaufen
        
            List <Film> filme = new ArrayList<>();
               for ( WochenplanDto.FilmDto filmDto : wochenplanDto.filme())
               { 
                filme.add(new Film(filmDto.titel(),
                 filmDto.laufzeit()));
                }
            List<Film> gespeicherteFilme = filmRepository.fuegeFilmeHinzu(filme);

            // filmtitel wir zu Schlüssel ID zum Wert
            // Filmtitel im Film domain ist vom Typ String?
            Map<String, FilmId> filmIdsNachTitel = gespeicherteFilme.stream()
                .collect(Collectors.toMap(film -> film.titel(), film -> film.id()));

        // Alle Filmvorstellungen als eigene fachliche Objekte der Filmvorführung erstellen
        // hier noch mit Jana abstimmen
        for (WochenplanDto.VorstellungDto vorstellung : wochenplanDto.vorstellungen()){
            // dannn das hier
            var filmvorfuehrung = new Filmvorfuehrung(
                    new VorstellungsId(vorstellung.id()),
                    new Saal(vorstellung.saalName()) ,

                    //new FilmId (filmRepository.getFilmByFilmtitel(vorstellung.filmtitel()),
                    // Alternative: die Map nutzen, anstatt des Repos
                    //FilmId filmId = filmIdsNachTitel.get(vorstellung.filmTitel());
                    //ersetzt durch FilmId new Filmtitel(vorstellung.filmtitel()) ,

                    new GeplanteStartzeit(vorstellung.geplanterStart()),
                    new GeplanteEndzeit(vorstellung.geplantesEnde()) //--> wird im wochenplanDto schon inklusive Werbeblock berechnet übergeben (Startzeit + 15 Min). Schnappen wir uns das?
                    //bzw. müssten wir das DTO erweitern, um zwischen Film und Werbeblock zu unterscheiden (Phase) (zur Anzeige in Filmvorführungen-Übersicht)
                    //Status=geplant hinzufügen
            );

            // Die Filmvorführung zum Speichern weitergeben.
            filmvorfuehrungRepository.fuegeVorfuehrungHinzu(filmvorfuehrung);
            // Filme über FilmRepo abspeichern /(Filmtitel und Filmlaufzeit)
            //im FilmRepo gibt es Methode holeFilmÜberTitel
        }
    }
}
