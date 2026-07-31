package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class HoleGewaehlteVorstellung implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleGewaehlteVorstellung {

    private final AktuelleVorstellungen aktuelleVorstellungen;

    @Override
    public Vorstellung fuer(VorstellungId vorstellungId) {
        return aktuelleVorstellungen.holeVorstellung(vorstellungId);
    }
}
