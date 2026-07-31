package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platzanzahl;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class SucheZusammenhaengendePlaetze implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.SucheZusammenhaengendePlaetze {

    private final SaalplanStapel saalplanStapel;

    @Override
    public ZusammenhaengendePlaetze fuer(VorstellungId vorstellungId, Platzanzahl platzanzahl) {
        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);
        return saalplan.sucheZusammenhaengendePlaetze(platzanzahl);
    }
}
