package de.wps.ddd.kino.einlass.infrastructure.messaging;

import de.wps.ddd.kino.einlass.service.ImportiereKinokartenVerkauftService;
import de.wps.ddd.kino.einlass.service.VerkaufteKinokarten;
import de.wps.ddd.kino.kartenverkauf.KinokartenVerkauftDto;
import lombok.RequiredArgsConstructor;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
/**
 * EventListener für die Verarbeitung von KinokartenVerkauftDto-Events.
 *
 * Dieser Listener empfängt Events, die den Verkauf von Kinokarten repräsentieren,
 * und leitet die Informationen an den entsprechenden Service weiter, um die verkauften
 * Kinokarten zu importieren.
 */
@Service
@RequiredArgsConstructor
public class KinokartenVerkauftEventListener {
    private final ImportiereKinokartenVerkauftService importiereKinokartenVerkauftService;

    @ApplicationModuleListener
    public void verarbeite(KinokartenVerkauftDto dto) {
        var karten = dto.karten().stream()
                .map(karte -> new VerkaufteKinokarten.Karte(
                        karte.kartenId(),
                        karte.reihe(),
                        karte.platz()))
                .toList();

        importiereKinokartenVerkauftService.importiere(
                new VerkaufteKinokarten(dto.vorstellungId(), dto.saal(), karten));
    }
}
