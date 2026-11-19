package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;

/* 
 * Phase, in der sich eine Filmvorführung befindet.
 * Geplante Unterbrechungen sind nicht vorgesehen.
 */

@ValueObject
public enum Phase {
    WERBUNG,
    FILM
}