package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model;

public record ZahlungsvorgangDto(
        String id,
        int anlauf,
        GeldbetragDto betrag,
        String status
) {
}
