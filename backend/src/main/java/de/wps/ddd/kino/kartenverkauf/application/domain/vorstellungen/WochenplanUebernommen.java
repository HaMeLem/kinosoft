package de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen;

import org.jmolecules.event.annotation.DomainEvent;

/**
 * Kartenverkauf-internes Ereignis: der Wochenplan wurde importiert (Vorstellungen persistiert, leere
 * Saalpläne angelegt). Auslöser für die {@code SaalplanBelegungFixture}, die anschließend die Demo-Belegung
 * ergänzt.
 */
@DomainEvent
public record WochenplanUebernommen(int jahr, int kalenderwoche) {
}
