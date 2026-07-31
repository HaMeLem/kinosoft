package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungAbgebrochen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import lombok.RequiredArgsConstructor;
import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class VerarbeiteZahlungsabbruch implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungsabbruch {

    private final Verkaufsvorgaenge verkaufsvorgaenge;

    @Override
    @DomainEventHandler
    public void fuer(ZahlungAbgebrochen zahlungAbgebrochen) {
        var verkaufsvorgang = verkaufsvorgaenge.holeZuZahlungsvorgang(zahlungAbgebrochen.zahlungsvorgangId());
        verkaufsvorgang.zahlungAbgebrochen(zahlungAbgebrochen.zahlungsvorgangId());
        verkaufsvorgaenge.speichere(verkaufsvorgang);
    }
}
