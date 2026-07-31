package de.wps.ddd.kino.zahlung;

import de.wps.ddd.kino.common.architecture.DomainCommand;

import java.util.UUID;

@DomainCommand
public record StarteZahlungDto(UUID referenz, long betragInCent) {
}
