package de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung;

import org.jmolecules.ddd.annotation.ValueObject;

/* 
 * Eine Filmvorführung kann geplant sein, unterbrochen sein, laufen oder beendet sein.
 */

@ValueObject
public enum Status {
    GEPLANT,
    UNTERBROCHEN,
    LAUFEND,
    ABGEBROCHEN,
    BEENDET
}