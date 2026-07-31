package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model;

public record VerkaufsvorgangDto(
        String auftragsnummer,
        String vorstellungId,
        ZusammenhaengendePlaetzeDto plaetze,
        GeldbetragDto gesamtpreis
) {
}
