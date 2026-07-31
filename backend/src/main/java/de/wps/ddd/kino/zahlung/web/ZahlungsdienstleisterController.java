package de.wps.ddd.kino.zahlung.web;

import de.wps.ddd.kino.zahlung.application.Zahlungsabwicklung;
import de.wps.ddd.kino.zahlung.domain.Zahlungen;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/zahlung")
@RequiredArgsConstructor
class ZahlungsdienstleisterController {

    private final Zahlungen zahlungen;
    private final Zahlungsabwicklung zahlungsabwicklung;
    private final ZahlungsdienstleisterDtoMapper zahlungMapper;

    @GetMapping("/{referenz}")
    ZahlungDto hole(@PathVariable UUID referenz) {
        return zahlungMapper.toDto(zahlungen.hole(new Zahlungsreferenz(referenz)));
    }

    @PostMapping("/{referenz}/bezahlen")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void bezahle(@PathVariable UUID referenz) {
        var zahlungsreferenz = new Zahlungsreferenz(referenz);
        zahlungen.hole(zahlungsreferenz);
        zahlungsabwicklung.bezahle(zahlungsreferenz);
    }
}
