package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Kinokarten;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class HoleKinokarten implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleKinokarten {

    private final Kinokarten kinokarten;

    @Override
    public List<Kinokarte> fuer(Auftragsnummer auftragsnummer) {
        var verkaufteKarten = kinokarten.finde(auftragsnummer);
        RessourceNichtGefunden.wenn(verkaufteKarten.isEmpty(),
                "Zu Auftrag " + auftragsnummer + " gibt es keine verkauften Kinokarten");
        return verkaufteKarten;
    }
}
