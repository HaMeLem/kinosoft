
package de.wps.ddd.kino.einlasskontrolle.infrastructure.web;

import de.wps.ddd.kino.einlasskontrolle.application.EinlasskontrolleService;
import de.wps.ddd.kino.einlasskontrolle.domain.Einlassentscheidung;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intern/einlass")
@RequiredArgsConstructor
public class EinlassController {

    private final EinlasskontrolleService service;

    @PostMapping("/kinokarte-validieren-und-entwerten")
    public ResponseEntity<?> kinokarteValidierenUndEntwerten(
             @RequestBody KinokarteValidierenUndEntwertenRequest request) {
        //var entscheidung = service.kinokarteValidierenUndEntwerten(new KinokartenId(request.kinokartenId()));

        // Einlass für Karte und Vorstellung prüfen, Karte ggf. entwerten
        var entscheidung = service.kinokarteValidierenUndEntwerten(
        new KinokartenId(request.kinokartenId()));

        if (entscheidung instanceof Einlassentscheidung.Erlaubt erlaubt) {
            return ResponseEntity.ok(new EinlassErlaubtDTO(
                    erlaubt.saal().wert(),
                    erlaubt.reihe().wert(),
                    erlaubt.sitzplatz().wert()));
        }

        if (entscheidung instanceof Einlassentscheidung.Abgelehnt abgelehnt) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new EinlassAbgelehntDTO(
                            abgelehnt.grund().name(),
                            abgelehnt.grund().meldung()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}