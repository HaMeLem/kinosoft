package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;

/* 
 * Eine Filmvorführung kann geplant sein, unterbrochen sein, laufen oder beendet sein.
 * Es wird nicht unterschieden, ob eine Filmvorführung erfolgreich beendet oder abgebrochen wurde.
 */

@ValueObject
public enum Status {
    GEPLANT,
    UNTERBROCHEN,
    LAUFEND,
    BEENDET
}