package de.wps.ddd.kino.einlass.infrastructure.web;

import de.wps.ddd.kino.einlass.domain.KinokartenId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intern/einlass")
@RequiredArgsConstructor
public class EinlassController {

    private final EinlassService service;

    @PostMapping("/kinokarte-validieren-und-entwerten")
    public KinokartenDTO kinokarteValidierenUndEntwerten(
            @RequestBody KinokarteValidierenUndEntwertenRequest request) {
        var kinokarte = service.kinokarteValidierenUndEntwerten(new KinokartenId(request.kinokartenId()));
        return new KinokartenDTO(
                kinokarte.getSaal().wert(),
                kinokarte.getReihe().wert(),
                kinokarte.getSitzplatz().wert());
    }
}
