
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
        new KinokartenId(request.kinokartenId()),
        new VorstellungsId(request.vorstellungsId()));

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

/*So sieht die Controller-Methode mit switch aus:

java
@PostMapping("/kinokarte-validieren-und-entwerten")
public ResponseEntity<?> kinokarteValidierenUndEntwerten(
        @RequestBody KinokarteValidierenUndEntwertenRequest request) {

    var entscheidung = service.kinokarteValidierenUndEntwerten(
            new KinokartenId(request.kinokartenId()),
            new VorstellungsId(request.vorstellungsId()));

    return switch (entscheidung) {
        case Einlassentscheidung.Erlaubt erlaubt ->
                ResponseEntity.ok(new EinlassErlaubtDTO(
                        erlaubt.saal().wert(),
                        erlaubt.reihe().wert(),
                        erlaubt.sitzplatz().wert()));

        case Einlassentscheidung.Abgelehnt abgelehnt ->
                ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new EinlassAbgelehntDTO(
                                abgelehnt.grund().name(),
                                abgelehnt.grund().meldung()));
    };
}

Der INTERNAL_SERVER_ERROR-Fallback am Ende fällt weg. Das Interface ist sealed und hat nur Erlaubt und Abgelehnt, 
deshalb braucht der switch keinen default-Zweig. Der Compiler prüft, dass alle Fälle abgedeckt sind. Fügst du später 
einen dritten Fall zu Einlassentscheidung hinzu, meldet er dir sofort einen Fehler im Controller.
*/