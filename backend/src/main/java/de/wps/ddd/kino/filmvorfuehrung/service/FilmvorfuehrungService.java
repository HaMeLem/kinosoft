package de.wps.ddd.kino.filmvorfuehrung.service;

import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

import org.jmolecules.ddd.annotation.Service;

@Service
public class FilmvorfuehrungService {

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
