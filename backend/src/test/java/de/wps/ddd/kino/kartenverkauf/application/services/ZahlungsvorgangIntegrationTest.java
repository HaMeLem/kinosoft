package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgangstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungAbgebrochen;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungEingegangen;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleZahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteVerkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteZahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungsabbruch;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungseingang;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ZahlungsvorgangIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private StarteVerkaufsvorgang starteVerkaufsvorgang;

    @Autowired
    private StarteZahlungsvorgang starteZahlungsvorgang;

    @Autowired
    private HoleZahlungsstatus holeZahlungsstatus;

    @Autowired
    private VerarbeiteZahlungseingang verarbeiteZahlungseingang;

    @Autowired
    private VerarbeiteZahlungsabbruch verarbeiteZahlungsabbruch;

    @Autowired
    private Verkaufsvorgaenge verkaufsvorgaenge;

    private final ZusammenhaengendePlaetze plaetze =
            new ZusammenhaengendePlaetze(List.of(new PlatzId(new ReiheNummer(4), new PlatzNummer(1))));

    private VorstellungId vorstellungId() {
        return vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));
    }

    private Auftragsnummer laufenderVorgang() {
        return starteVerkaufsvorgang.fuer(vorstellungId(), plaetze).getAuftragsnummer();
    }

    @Test
    void starteVerkaufsvorgang_ermitteltDenPreisSelbstUndLegtNochKeinenZahlungsvorgangAn() {
        // act
        var verkaufsvorgang = starteVerkaufsvorgang.fuer(vorstellungId(), plaetze);

        // assert
        assertThat(verkaufsvorgang.getGesamtpreis().getBetrag()).isPositive();
        assertThat(verkaufsvorgang.getStatus()).isEqualTo(Verkaufsvorgangstatus.Laufend);
        assertThat(verkaufsvorgang.zahlungsvorgang()).isEmpty();

        assertThatThrownBy(() -> holeZahlungsstatus.fuer(verkaufsvorgang.getAuftragsnummer()))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("noch keinen Zahlungsvorgang");
    }

    @Test
    void starteZahlungsvorgang_ersterZahlungsvorgang_ueberDenVereinbartenPreis() {
        // arrange
        var auftragsnummer = laufenderVorgang();

        // act
        var zahlungsvorgang = starteZahlungsvorgang.fuer(auftragsnummer);

        // assert
        assertThat(zahlungsvorgang.getAnlauf()).isEqualTo(1);
        assertThat(zahlungsvorgang.getBetrag()).isEqualTo(verkaufsvorgaenge.hole(auftragsnummer).getGesamtpreis());
        assertThat(holeZahlungsstatus.fuer(auftragsnummer)).isEqualTo(Zahlungsstatus.Ausstehend);
    }

    @Test
    void starteZahlungsvorgang_nachAbbruch_ersetztIhnZumSelbenPreis() {
        // arrange
        var auftragsnummer = laufenderVorgang();
        var vereinbarterPreis = verkaufsvorgaenge.hole(auftragsnummer).getGesamtpreis();
        var ersterZahlungsvorgang = starteZahlungsvorgang.fuer(auftragsnummer);
        verarbeiteZahlungsabbruch.fuer(new ZahlungAbgebrochen(ersterZahlungsvorgang.getId()));

        // act
        var zweiterZahlungsvorgang = starteZahlungsvorgang.fuer(auftragsnummer);

        // assert
        assertThat(zweiterZahlungsvorgang.getAnlauf()).isEqualTo(2);
        assertThat(zweiterZahlungsvorgang.getId()).isNotEqualTo(ersterZahlungsvorgang.getId());
        assertThat(zweiterZahlungsvorgang.getBetrag()).isEqualTo(vereinbarterPreis);
        assertThat(holeZahlungsstatus.fuer(auftragsnummer)).isEqualTo(Zahlungsstatus.Ausstehend);

        var gespeichert = verkaufsvorgaenge.hole(auftragsnummer);
        assertThat(gespeichert.zahlungsvorgang()).get()
                .extracting(Zahlungsvorgang::getId).isEqualTo(zweiterZahlungsvorgang.getId());
        assertThat(gespeichert.getAnlaeufe()).isEqualTo(2);

        // und der zweite Zahlungsvorgang schließt den Verkauf ab
        verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(zweiterZahlungsvorgang.getId()));
        assertThat(holeZahlungsstatus.fuer(auftragsnummer)).isEqualTo(Zahlungsstatus.Eingegangen);
        assertThat(verkaufsvorgaenge.hole(auftragsnummer).getStatus())
                .isEqualTo(Verkaufsvorgangstatus.Abgeschlossen);
    }

    @Test
    void verarbeiteZahlungsabbruch_laesstDenVerkaufsvorgangLaufen() {
        // arrange
        var auftragsnummer = laufenderVorgang();
        var zahlungsvorgang = starteZahlungsvorgang.fuer(auftragsnummer);

        // act
        verarbeiteZahlungsabbruch.fuer(new ZahlungAbgebrochen(zahlungsvorgang.getId()));

        // assert
        assertThat(holeZahlungsstatus.fuer(auftragsnummer)).isEqualTo(Zahlungsstatus.Abgebrochen);
        assertThat(verkaufsvorgaenge.hole(auftragsnummer).getStatus())
                .isEqualTo(Verkaufsvorgangstatus.Laufend);
    }

    @Test
    void starteZahlungsvorgang_waehrendEinZahlungsvorgangLaeuft_wirftGeschaeftsregelVerletzt() {
        // arrange
        var auftragsnummer = laufenderVorgang();
        starteZahlungsvorgang.fuer(auftragsnummer);

        // act / assert
        assertThatThrownBy(() -> starteZahlungsvorgang.fuer(auftragsnummer))
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("Es läuft schon ein Zahlungsvorgang");
    }

    @Test
    void starteZahlungsvorgang_nachAbschluss_wirftGeschaeftsregelVerletzt() {
        // arrange
        var auftragsnummer = laufenderVorgang();
        var zahlungsvorgang = starteZahlungsvorgang.fuer(auftragsnummer);
        verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(zahlungsvorgang.getId()));

        // act / assert
        assertThatThrownBy(() -> starteZahlungsvorgang.fuer(auftragsnummer))
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("abgeschlossener Verkaufsvorgang");
    }

    @Test
    void starteZahlungsvorgang_unbekannteAuftragsnummer_wirftRessourceNichtGefunden() {
        // arrange
        var unbekannteAuftragsnummer = new Auftragsnummer(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> starteZahlungsvorgang.fuer(unbekannteAuftragsnummer))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert nicht");
    }

    @Test
    void verarbeiteZahlungseingang_unbekannterZahlungsvorgang_wirftRessourceNichtGefunden() {
        // arrange
        var unbekannterZahlungsvorgang = ZahlungsvorgangId.neu();

        // act / assert
        assertThatThrownBy(() -> verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(unbekannterZahlungsvorgang)))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert kein Verkaufsvorgang");
    }

    @Test
    void holeZahlungsstatus_unbekannteAuftragsnummer_wirftRessourceNichtGefunden() {
        // arrange
        var unbekannteAuftragsnummer = new Auftragsnummer(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> holeZahlungsstatus.fuer(unbekannteAuftragsnummer))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert nicht");
    }
}
