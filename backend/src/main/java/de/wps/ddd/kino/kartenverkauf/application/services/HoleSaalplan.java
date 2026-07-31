package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class HoleSaalplan implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleSaalplan {

    private final SaalplanStapel saalplanStapel;

    @Override
    public Saalplan fuer(VorstellungId vorstellungId) {
        return saalplanStapel.holeSaalplan(vorstellungId);
    }
}
