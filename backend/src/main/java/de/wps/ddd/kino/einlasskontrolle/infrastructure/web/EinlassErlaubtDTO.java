package de.wps.ddd.kino.einlasskontrolle.infrastructure.web;

public record EinlassErlaubtDTO(
        int saal,
        int reihe,
        int sitzplatz
) {
}
