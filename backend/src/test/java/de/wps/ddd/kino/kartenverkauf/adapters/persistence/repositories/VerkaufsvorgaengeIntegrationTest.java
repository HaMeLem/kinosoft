package de.wps.ddd.kino.kartenverkauf.adapters.persistence.repositories;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgangstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class VerkaufsvorgaengeIntegrationTest {

    @Autowired
    private Verkaufsvorgaenge verkaufsvorgaenge;

    @Test
    void speichereUndHole_liestVorstellungPlaetzeUndStatusZurueck() {
        // arrange
        var vorstellungId = new VorstellungId(UUID.randomUUID());
        var verkaufsvorgang = Verkaufsvorgang.starte(vorstellungId,
                new ZusammenhaengendePlaetze(List.of(platzId(4, 1), platzId(4, 2))),
                Geldbetrag.euro(25, 0));

        // act
        verkaufsvorgaenge.speichere(verkaufsvorgang);
        var geladen = verkaufsvorgaenge.hole(verkaufsvorgang.getAuftragsnummer());

        // assert
        assertThat(geladen.getAuftragsnummer()).isEqualTo(verkaufsvorgang.getAuftragsnummer());
        assertThat(geladen.getVorstellungId()).isEqualTo(vorstellungId);
        assertThat(geladen.getGewaehltePlaetze().plaetze())
                .containsExactlyInAnyOrder(platzId(4, 1), platzId(4, 2));
        assertThat(geladen.getGesamtpreis()).isEqualTo(Geldbetrag.euro(25, 0));
        assertThat(geladen.getStatus()).isEqualTo(Verkaufsvorgangstatus.Laufend);
    }

    @Test
    void speichere_haeltDenAktuellenZahlungsvorgangUndDenAbschlussFest() {
        // arrange
        var verkaufsvorgang = Verkaufsvorgang.starte(new VorstellungId(UUID.randomUUID()),
                new ZusammenhaengendePlaetze(List.of(platzId(4, 1))), Geldbetrag.euro(12, 50));
        var ersterZahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgang.zahlungAbgebrochen(ersterZahlungsvorgang.getId());
        var zweiterZahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgang.zahlungEingegangen(zweiterZahlungsvorgang.getId());

        // act
        verkaufsvorgang.schliesseAb();
        verkaufsvorgaenge.speichere(verkaufsvorgang);

        // assert
        var geladen = verkaufsvorgaenge.hole(verkaufsvorgang.getAuftragsnummer());
        assertThat(geladen.getStatus()).isEqualTo(Verkaufsvorgangstatus.Abgeschlossen);
        assertThat(geladen.istLaufend()).isFalse();
        assertThat(geladen.getAnlaeufe()).isEqualTo(2);
        assertThat(geladen.zahlungsvorgang()).get().satisfies(zahlungsvorgang -> {
            assertThat(zahlungsvorgang.getId()).isEqualTo(zweiterZahlungsvorgang.getId());
            assertThat(zahlungsvorgang.getAnlauf()).isEqualTo(2);
            assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Eingegangen);
            assertThat(zahlungsvorgang.getBetrag()).isEqualTo(Geldbetrag.euro(12, 50));
        });
    }

    @Test
    void holeZuZahlungsvorgang_findetDenVorgangUeberDieZahlungsvorgangId() {
        // arrange
        var verkaufsvorgang = Verkaufsvorgang.starte(new VorstellungId(UUID.randomUUID()),
                new ZusammenhaengendePlaetze(List.of(platzId(4, 1))), Geldbetrag.euro(12, 50));
        var zahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        verkaufsvorgaenge.speichere(verkaufsvorgang);

        // act / assert
        assertThat(verkaufsvorgaenge.holeZuZahlungsvorgang(zahlungsvorgang.getId()).getAuftragsnummer())
                .isEqualTo(verkaufsvorgang.getAuftragsnummer());
    }

    @Test
    void holeZuZahlungsvorgang_unbekannterZahlungsvorgang_wirftRessourceNichtGefunden() {
        assertThatThrownBy(() -> verkaufsvorgaenge.holeZuZahlungsvorgang(ZahlungsvorgangId.neu()))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert kein Verkaufsvorgang");
    }

    @Test
    void hole_unbekannteAuftragsnummer_wirftRessourceNichtGefunden() {
        // arrange
        var unbekannteAuftragsnummer = new Auftragsnummer(UUID.randomUUID());

        // act / assert
        assertThatThrownBy(() -> verkaufsvorgaenge.hole(unbekannteAuftragsnummer))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert nicht");
    }

    private static PlatzId platzId(int reihe, int platz) {
        return new PlatzId(new ReiheNummer(reihe), new PlatzNummer(platz));
    }
}
