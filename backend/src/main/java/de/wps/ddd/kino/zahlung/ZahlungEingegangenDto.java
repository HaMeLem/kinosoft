package de.wps.ddd.kino.zahlung;

import org.jmolecules.event.annotation.DomainEvent;

import java.util.UUID;

@DomainEvent
public record ZahlungEingegangenDto(UUID referenz) {
}
