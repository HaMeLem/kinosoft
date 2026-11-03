package de.wps.ddd.kino.einlasskontrolle.infrastructure.messaging;

import de.wps.ddd.kino.einlasskontrolle.application.ImportiereKinokartenVerkauftService;
import de.wps.ddd.kino.einlasskontrolle.application.VerkaufteKinokarten;
import de.wps.ddd.kino.kartenverkauf.KinokartenVerkauftDto;
import lombok.RequiredArgsConstructor;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EventListener für die Verarbeitung von KinokartenVerkauftDto-Events.
 *
 * Dieser Listener empfängt Events, die den Verkauf von Kinokarten repräsentieren,
 * und leitet die Informationen an den entsprechenden Service weiter, um die verkauften
 * Kinokarten zu importieren.
 */
@Service
@RequiredArgsConstructor
public class KinokartenVerkauftEventListener
{
    private final ImportiereKinokartenVerkauftService importiereKinokartenVerkauftService;

    @ApplicationModuleListener
    public void verarbeite(KinokartenVerkauftDto verkaufsereignis)
    //Methode erwartet das KinokartenVerkauftDto (wir nennen es hier verkaufsereignis), das aus der Subdomäne Kartenverkauf kommt (adapters, secondary, eventpublisher)
    {
        // Überträgt die Karten aus dem Verkaufsereignis in das Modell der Einlasskontrolle.
        // wir eröffnen einen stream aus Karten aus dem KinokartenVerkauftDto, das wir hier verkaufsereignis nennen
        // mit .map  wandelt jedes Element des bestehenden Streams in ein VerkaufteKinokarten.Karte-Objekt um
        //beenden den Stream mit .toList() und speichern dies in einer Liste von VerkaufteKinkokarten.Karte- Objekten namens karten
        List<VerkaufteKinokarten.Karte> karten = verkaufsereignis.karten().stream()
                .map(karte -> new VerkaufteKinokarten.Karte(
                        karte.kartenId(),
                        karte.reihe(),
                        karte.platz()))
                .toList();
         // Übergibt die Verkaufsdaten zum Import an den zuständigen Service.
        //rufen den zugehörigen Service zum importieren auf und übergibt ein neues VerkaufteKinokarten-Objekt mit Vorst.id, Saal und Kartenliste
        importiereKinokartenVerkauftService.importiere(
                new VerkaufteKinokarten(verkaufsereignis.vorstellungId(), verkaufsereignis.saal(), karten));
    }
}
