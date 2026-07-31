package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KartenBlock;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KinokartenVerkauft;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungEingegangen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Kinokarten;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsmeldungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import lombok.RequiredArgsConstructor;
import org.jmolecules.event.annotation.DomainEventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class VerarbeiteZahlungseingang implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungseingang {

    private final Verkaufsvorgaenge verkaufsvorgaenge;

    private final AktuelleVorstellungen aktuelleVorstellungen;

    private final KartenBlock kartenBlock;

    private final SaalplanStapel saalplanStapel;

    private final Kinokarten kinokarten;

    private final Verkaufsmeldungen verkaufsmeldungen;

    @Override
    @DomainEventHandler
    public void fuer(ZahlungEingegangen zahlungEingegangen) {
        var verkaufsvorgang = verkaufsvorgaenge.holeZuZahlungsvorgang(zahlungEingegangen.zahlungsvorgangId());
        verkaufsvorgang.zahlungEingegangen(zahlungEingegangen.zahlungsvorgangId());

        var auftragsnummer = verkaufsvorgang.getAuftragsnummer();
        var vorstellungId = verkaufsvorgang.getVorstellungId();
        var gewaehltePlaetze = verkaufsvorgang.getGewaehltePlaetze();

        var vorstellung = aktuelleVorstellungen.holeVorstellung(vorstellungId);
        var erzeugteKarten = kartenBlock.erstelleKarten(auftragsnummer, vorstellung, gewaehltePlaetze);

        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);
        saalplan.markiereAlsVerkauft(gewaehltePlaetze);
        saalplanStapel.legeZurueck(saalplan);

        verkaufsvorgang.schliesseAb();

        kinokarten.speichere(erzeugteKarten);
        verkaufsvorgaenge.speichere(verkaufsvorgang);
        verkaufsmeldungen.melde(new KinokartenVerkauft(auftragsnummer, vorstellung, erzeugteKarten));
    }
}
