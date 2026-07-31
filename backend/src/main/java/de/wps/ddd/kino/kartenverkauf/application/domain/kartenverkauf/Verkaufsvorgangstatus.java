package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Einen Abbruch gibt es bewusst nicht: ein abgelehnter Zahlungsvorgang darf wiederholt werden.
 * Verwaiste Vorgänge bleiben deshalb offen — sie bräuchten ein Verfallsdatum.
 */
@ValueObject
public enum Verkaufsvorgangstatus {
    Laufend,
    Abgeschlossen,
}
