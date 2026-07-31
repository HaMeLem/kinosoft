package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerkaufsvorgangTest {

    @Test
    void starte_haeltBestellungUndPreisFestUndBeginntOhneZahlungsvorgang() {
        // act
        var verkaufsvorgang = starte();

        // assert
        assertThat(verkaufsvorgang.getAuftragsnummer()).isNotNull();
        assertThat(verkaufsvorgang.getGesamtpreis()).isEqualTo(Geldbetrag.euro(25, 0));
        assertThat(verkaufsvorgang.getGewaehltePlaetze().plaetze()).hasSize(2);
        assertThat(verkaufsvorgang.getStatus()).isEqualTo(Verkaufsvorgangstatus.Laufend);
        assertThat(verkaufsvorgang.zahlungsvorgang()).isEmpty();
        assertThat(verkaufsvorgang.getAnlaeufe()).isZero();
        assertThat(verkaufsvorgang.istBezahlt()).isFalse();
    }

    @Test
    void starteZahlungsvorgang_ersterZahlungsvorgang_ueberDenGesamtpreis() {
        // arrange
        var verkaufsvorgang = starte();

        // act
        var zahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();

        // assert
        assertThat(zahlungsvorgang.getAnlauf()).isEqualTo(1);
        assertThat(zahlungsvorgang.getBetrag()).isEqualTo(verkaufsvorgang.getGesamtpreis());
        assertThat(verkaufsvorgang.zahlungsvorgang()).contains(zahlungsvorgang);
        assertThat(verkaufsvorgang.getAnlaeufe()).isEqualTo(1);
    }

    @Test
    void starteZahlungsvorgang_nachAbbruch_ersetztDenGescheitertenAnlauf() {
        // arrange
        var verkaufsvorgang = starte();
        var ersterZahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgang.zahlungAbgebrochen(ersterZahlungsvorgang.getId());

        // act
        var zweiterZahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();

        // assert
        assertThat(zweiterZahlungsvorgang.getAnlauf()).isEqualTo(2);
        assertThat(zweiterZahlungsvorgang.getId()).isNotEqualTo(ersterZahlungsvorgang.getId());
        assertThat(zweiterZahlungsvorgang.getBetrag()).isEqualTo(ersterZahlungsvorgang.getBetrag());
        assertThat(ersterZahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Abgebrochen);
        assertThat(verkaufsvorgang.zahlungsvorgang()).contains(zweiterZahlungsvorgang);
        assertThat(verkaufsvorgang.getAnlaeufe()).isEqualTo(2);
    }

    @Test
    void starteZahlungsvorgang_waehrendEinerOffenIst_wirftGeschaeftsregelVerletzt() {
        // arrange
        var verkaufsvorgang = starte();
        verkaufsvorgang.starteZahlungsvorgang();

        // act / assert
        assertThatThrownBy(verkaufsvorgang::starteZahlungsvorgang)
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("Es läuft schon ein Zahlungsvorgang");
    }

    @Test
    void starteZahlungsvorgang_nachAbschluss_wirftGeschaeftsregelVerletzt() {
        // arrange
        var verkaufsvorgang = bezahlterVorgang();
        verkaufsvorgang.schliesseAb();

        // act / assert
        assertThatThrownBy(verkaufsvorgang::starteZahlungsvorgang)
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("abgeschlossener Verkaufsvorgang");
    }

    @Test
    void zahlungEingegangen_fremderZahlungsvorgang_wirftRessourceNichtGefunden() {
        // arrange
        var verkaufsvorgang = starte();
        verkaufsvorgang.starteZahlungsvorgang();

        // act / assert
        assertThatThrownBy(() -> verkaufsvorgang.zahlungEingegangen(ZahlungsvorgangId.neu()))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("nicht der aktuelle Zahlungsvorgang");
    }

    @Test
    void schliesseAb_nachZahlungseingang_beendetDenVorgang() {
        // arrange
        var verkaufsvorgang = bezahlterVorgang();

        // act
        verkaufsvorgang.schliesseAb();

        // assert
        assertThat(verkaufsvorgang.getStatus()).isEqualTo(Verkaufsvorgangstatus.Abgeschlossen);
        assertThat(verkaufsvorgang.istLaufend()).isFalse();
    }

    @Test
    void schliesseAb_ohneZahlungseingang_wirftGeschaeftsregelVerletzt() {
        // arrange
        var verkaufsvorgang = starte();
        verkaufsvorgang.starteZahlungsvorgang();

        // act / assert
        assertThatThrownBy(verkaufsvorgang::schliesseAb)
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("Zahlung ist noch nicht eingegangen");
    }

    @Test
    void schliesseAb_zweimal_wirftGeschaeftsregelVerletzt() {
        // arrange
        var verkaufsvorgang = bezahlterVorgang();
        verkaufsvorgang.schliesseAb();

        // act / assert
        assertThatThrownBy(verkaufsvorgang::schliesseAb)
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("Nur laufende Verkaufsvorgänge");
    }

    @Test
    void zahlungAbgebrochen_fuerEinenErsetztenAnlauf_wirftRessourceNichtGefunden() {
        // arrange
        var verkaufsvorgang = starte();
        var ersterZahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgang.zahlungAbgebrochen(ersterZahlungsvorgang.getId());
        verkaufsvorgang.starteZahlungsvorgang();

        // act / assert
        assertThatThrownBy(() -> verkaufsvorgang.zahlungAbgebrochen(ersterZahlungsvorgang.getId()))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("nicht der aktuelle Zahlungsvorgang");
    }

    private static Verkaufsvorgang starte() {
        return Verkaufsvorgang.starte(
                new VorstellungId(UUID.randomUUID()),
                new ZusammenhaengendePlaetze(List.of(
                        new PlatzId(new ReiheNummer(4), new PlatzNummer(1)),
                        new PlatzId(new ReiheNummer(4), new PlatzNummer(2)))),
                Geldbetrag.euro(25, 0));
    }

    private static Verkaufsvorgang bezahlterVorgang() {
        var verkaufsvorgang = starte();
        var zahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgang.zahlungEingegangen(zahlungsvorgang.getId());
        return verkaufsvorgang;
    }
}
