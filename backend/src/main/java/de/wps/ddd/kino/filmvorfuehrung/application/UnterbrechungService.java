package de.wps.ddd.kino.filmvorfuehrung.application;

import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;

import org.jmolecules.ddd.annotation.Service;

@Service
public class UnterbrechungService {

    private void beginneUnterbrechung(
        VorfuehrungId vorfuehrungId, 
        Unterbrechungsbeginn unterbrechungsbeginn) {
            /* Ergänzen, wenn es ein Repository gibt:
            * - Vorfuehrung mit der ID holen
            * - Status auf Unterbrochen setzen
            * - neuer Unterbrechung Beginn und ID zuweisen
            * - Unterbrechung speichern */ 
    }

    private void beendeUnterbrechung(
        VorfuehrungId vorfuehrungId, 
        Unterbrechungsende unterbrechungsende) {
            /* Ergänzen, wenn es ein Repository gibt:
            * - Vorfuehrung mit der ID holen
            * - Status auf Laufend setzen
            * - Unterbrechung mit Unterbrechungsende null suchen
            * - Unterbrechungsende speichern */ 
    }
}
