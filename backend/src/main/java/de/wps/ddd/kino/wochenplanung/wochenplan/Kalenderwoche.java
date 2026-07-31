package de.wps.ddd.kino.wochenplanung.wochenplan;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Kalenderwoche(int jahr, int kalenderwoche) {
}
