package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class StarteVerkaufsvorgang implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteVerkaufsvorgang {

    private final Verkaufsvorgaenge verkaufsvorgaenge;

    private final BerechneGesamtpreis berechneGesamtpreis;

    @Override
    public Verkaufsvorgang fuer(VorstellungId vorstellungId, ZusammenhaengendePlaetze gewaehltePlaetze) {
        var gesamtpreis = berechneGesamtpreis.fuer(vorstellungId, gewaehltePlaetze);

        var verkaufsvorgang = Verkaufsvorgang.starte(vorstellungId, gewaehltePlaetze, gesamtpreis);
        verkaufsvorgaenge.speichere(verkaufsvorgang);
        // TODO
        // Plätze im Saalplan blocken, damit sie nicht doppelt verkauft werden
        return verkaufsvorgang;
    }
}
