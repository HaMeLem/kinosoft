package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Zahlungsdienstleister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class StarteZahlungsvorgang implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteZahlungsvorgang {

    private final Verkaufsvorgaenge verkaufsvorgaenge;

    private final Zahlungsdienstleister zahlungsdienstleister;

    @Override
    public Zahlungsvorgang fuer(Auftragsnummer auftragsnummer) {
        var verkaufsvorgang = verkaufsvorgaenge.hole(auftragsnummer);
        var zahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgaenge.speichere(verkaufsvorgang);
        zahlungsdienstleister.starteZahlung(zahlungsvorgang.getId(), zahlungsvorgang.getBetrag());
        return zahlungsvorgang;
    }
}
