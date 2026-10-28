package de.wps.ddd.kino.einlasskontrolle.infrastructure.web;

public record KinokartenDTO(
        int saal,
        int reihe,
        int sitzplatz
) {
}
