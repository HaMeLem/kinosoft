package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum Zahlungsstatus {
    Ausstehend,
    Eingegangen,
    Abgebrochen,
}
