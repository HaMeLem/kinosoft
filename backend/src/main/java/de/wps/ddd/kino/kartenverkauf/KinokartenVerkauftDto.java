package de.wps.ddd.kino.kartenverkauf;

import org.jmolecules.event.annotation.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DomainEvent
public record KinokartenVerkauftDto(
        UUID auftragsnummer,
        UUID vorstellungId,
        String film,
        LocalDateTime beginn,
        String saal,
        List<VerkaufteKarte> karten) {

    public record VerkaufteKarte(UUID kartenId, int reihe, int platz) {
    }
}
