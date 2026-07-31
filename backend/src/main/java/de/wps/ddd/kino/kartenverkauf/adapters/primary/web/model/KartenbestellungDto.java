package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model;

import java.util.UUID;

public record KartenbestellungDto(UUID vorstellungId, ZusammenhaengendePlaetzeDto plaetze) {
}
