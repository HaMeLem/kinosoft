package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model;

import java.util.UUID;

public record KinokarteDto(
        String film,
        String beginn,
        String saal,
        int reihe,
        int platz,
        UUID id
) {
}
