package de.wps.ddd.kino.filmvorfuehrung.application;

import org.springframework.stereotype.Service;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmvorfuehrung.domain.FilmvorfuehrungRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;


@ApplicationService 
@Service
public class FilmvorfuehrungService {

     private final FilmvorfuehrungRepository filmvorfuehrungRepository;
     private final FilmRepository filmRepository;

     private void beginneFilmvorfuehrung(VorfuehrungId vorfuehrungId) {
        /* Ergänzen, wenn es ein Repository gibt:
         * - Vorfuehrung mit der ID holen
         * - Status auf Laufend setzen
         * - für TatsaechlicherBeginn LocalDateTime.now() setzen */ 
     }

     private void beendeFilmvorfuehrung(VorfuehrungId vorfuehrungId, Unterbrechungsende unterbrechungsende) {
        /* Ergänzen, wenn es ein Repository gibt:
         * - Vorfuehrung mit der ID holen
         * - Status auf Beendet setzen */ 
     }   
}