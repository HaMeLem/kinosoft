package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import de.wps.ddd.kino.kartenverkauf.KinokartenVerkauftDto;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungEingegangen;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleKinokarten;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteVerkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteZahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungseingang;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.modulith.test.PublishedEventsExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Die {@link PublishedEventsExtension} löst den {@link AssertablePublishedEvents}-Parameter auf —
 * bei einem reinen {@code @SpringBootTest} wird sie (anders als bei {@code @ApplicationModuleTest})
 * nicht automatisch registriert.
 */
@SpringBootTest
@Transactional
@ExtendWith(PublishedEventsExtension.class)
class VerarbeiteZahlungseingangIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private StarteVerkaufsvorgang starteVerkaufsvorgang;

    @Autowired
    private StarteZahlungsvorgang starteZahlungsvorgang;

    @Autowired
    private VerarbeiteZahlungseingang verarbeiteZahlungseingang;

    @Autowired
    private de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge verkaufsvorgaenge;

    @Autowired
    private HoleKinokarten holeKinokarten;

    private final ZusammenhaengendePlaetze gewaehltePlaetze = new ZusammenhaengendePlaetze(List.of(
            platzId(4, 1), platzId(4, 2), platzId(4, 3), platzId(4, 4)));

    @Test
    void zahlungEingegangen_schliesstDenVerkaufAbUndLiefertDieBestelltenKarten(
            AssertablePublishedEvents veroeffentlichteEvents) {
        // arrange
        var vorstellungId = vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));
        var auftragsnummer = starteVerkaufsvorgang.fuer(vorstellungId, gewaehltePlaetze).getAuftragsnummer();
        var zahlungsvorgangId = starteZahlungsvorgang.fuer(auftragsnummer).getId();

        // act
        verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(zahlungsvorgangId));

        // assert
        var kinokarten = holeKinokarten.fuer(auftragsnummer);
        assertThat(kinokarten).hasSize(4);
        assertThat(kinokarten).allSatisfy(karte -> {
            assertThat(karte.getAuftragsnummer()).isEqualTo(auftragsnummer);
            assertThat(karte.getVorstellungId()).isEqualTo(vorstellungId);
            assertThat(karte.getFilm().name()).isEqualTo("The Fast and the Curious");
            assertThat(karte.getSaal().name()).isEqualTo("kleiner Saal");
            assertThat(karte.getReihe().nummer()).isEqualTo(4);
        });
        assertThat(kinokarten).extracting(karte -> karte.getPlatz().nummer())
                .containsExactlyInAnyOrder(1, 2, 3, 4);

        veroeffentlichteEvents.assertThat().contains(KinokartenVerkauftDto.class)
                .matching(event -> event.auftragsnummer().equals(auftragsnummer.nummer()))
                .matching(event -> event.vorstellungId().equals(vorstellungId.uuid()))
                .matching(event -> event.film().equals("The Fast and the Curious"))
                .matching(event -> event.karten().size() == 4);
    }

    @Test
    void zahlungEingegangen_zweimal_stelltKeineZweitenKartenAus() {
        // arrange
        var vorstellungId = vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));
        var auftragsnummer = starteVerkaufsvorgang.fuer(vorstellungId, gewaehltePlaetze).getAuftragsnummer();
        var zahlungsvorgangId = starteZahlungsvorgang.fuer(auftragsnummer).getId();
        verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(zahlungsvorgangId));

        // act / assert
        assertThatThrownBy(() -> verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(zahlungsvorgangId)))
                .isInstanceOf(GeschaeftsregelVerletzt.class);
        assertThat(holeKinokarten.fuer(auftragsnummer)).hasSize(4);
        assertThat(verkaufsvorgaenge.hole(auftragsnummer).istLaufend()).isFalse();
    }

    private static PlatzId platzId(int reihe, int platz) {
        return new PlatzId(new ReiheNummer(reihe), new PlatzNummer(platz));
    }
}
