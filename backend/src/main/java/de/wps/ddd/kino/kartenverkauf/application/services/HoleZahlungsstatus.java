package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class HoleZahlungsstatus implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleZahlungsstatus {

    private final Verkaufsvorgaenge verkaufsvorgaenge;

    @Override
    public Zahlungsstatus fuer(Auftragsnummer auftragsnummer) {
        var zahlungsvorgang = verkaufsvorgaenge.hole(auftragsnummer).zahlungsvorgang();
        RessourceNichtGefunden.wenn(zahlungsvorgang.isEmpty(),
                "Zu Auftrag " + auftragsnummer + " gibt es noch keinen Zahlungsvorgang");
        return zahlungsvorgang.get().getStatus();
    }
}
