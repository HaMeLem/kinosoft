package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.common.architecture.Gateway;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;

import java.util.List;

/**
 * Sekundärport (Gateway) zur Wochenplanung: liefert die Vorstellungen der Kalenderwoche bereits in
 * die Kartenverkauf-Domäne übersetzt. Der Adapter kapselt die Übersetzung des exponierten
 * {@code WochenplanDto} (Anti-Corruption-Layer). Die Bestuhlung stammt NICHT von hier, sondern aus
 * den kartenverkauf-eigenen Stammdaten ({@link Saele}).
 */
@Gateway
public interface Wochenplaene {

    List<Vorstellung> holeWochenplan(int jahr, int kalenderwoche);
}
