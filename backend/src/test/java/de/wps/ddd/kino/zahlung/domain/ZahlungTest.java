package de.wps.ddd.kino.zahlung.domain;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZahlungTest {

    private final Zahlungsreferenz referenz = new Zahlungsreferenz(UUID.randomUUID());

    @Test
    void neueZahlung_istOffen() {
        // act
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000));

        // assert
        assertThat(zahlung.getStatus()).isEqualTo(Zahlungsstatus.Offen);
        assertThat(zahlung.getReferenz()).isEqualTo(referenz);
        assertThat(zahlung.getBetrag()).isEqualTo(new Betrag(5000));
    }

    @Test
    void eingegangen_ausOffen_wirdEingegangen() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000));

        // act
        zahlung.eingegangen();

        // assert
        assertThat(zahlung.getStatus()).isEqualTo(Zahlungsstatus.Eingegangen);
    }

    @Test
    void abgebrochen_ausOffen_wirdAbgebrochen() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000));

        // act
        zahlung.abgebrochen();

        // assert
        assertThat(zahlung.getStatus()).isEqualTo(Zahlungsstatus.Abgebrochen);
    }

    @Test
    void eingegangen_bereitsEingegangen_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000)).eingegangen();

        // act / assert
        assertThatThrownBy(zahlung::eingegangen).isInstanceOf(GeschaeftsregelVerletzt.class);
    }

    @Test
    void eingegangen_bereitsAbgebrochen_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000)).abgebrochen();

        // act / assert
        assertThatThrownBy(zahlung::eingegangen).isInstanceOf(GeschaeftsregelVerletzt.class);
    }

    @Test
    void abgebrochen_bereitsEingegangen_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000)).eingegangen();

        // act / assert
        assertThatThrownBy(zahlung::abgebrochen).isInstanceOf(GeschaeftsregelVerletzt.class);
    }

    @Test
    void abgebrochen_bereitsAbgebrochen_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000)).abgebrochen();

        // act / assert
        assertThatThrownBy(zahlung::abgebrochen).isInstanceOf(GeschaeftsregelVerletzt.class);
    }
}
