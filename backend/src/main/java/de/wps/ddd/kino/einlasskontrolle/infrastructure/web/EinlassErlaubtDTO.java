package de.wps.ddd.kino.einlasskontrolle.infrastructure.web;

public record EinlassErlaubtDTO(
        String typ,
        int saal,
        int reihe,
        int sitzplatz
) {
}
