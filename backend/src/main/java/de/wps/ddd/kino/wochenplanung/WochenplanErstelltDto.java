package de.wps.ddd.kino.wochenplanung;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record WochenplanErstelltDto(int jahr, int kalenderwoche) {
}
