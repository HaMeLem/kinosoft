package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Preisberechnung;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class BerechneGesamtpreis implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.BerechneGesamtpreis {

    private final AktuelleVorstellungen aktuelleVorstellungen;

    private final SaalplanStapel saalplanStapel;

    private final Preisberechnung preisberechnung;

    @Override
    public Geldbetrag fuer(VorstellungId vorstellungId, ZusammenhaengendePlaetze zusammenhaengendePlaetze) {
        var vorstellung = aktuelleVorstellungen.holeVorstellung(vorstellungId);
        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);
        return preisberechnung.ermittlePreis(vorstellung, saalplan, zusammenhaengendePlaetze);
    }
}
