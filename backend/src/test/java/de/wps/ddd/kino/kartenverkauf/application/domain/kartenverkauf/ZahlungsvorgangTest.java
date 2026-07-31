package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZahlungsvorgangTest {

    private static Zahlungsvorgang zahlungsvorgang() {
        return Zahlungsvorgang.starte(1, Geldbetrag.euro(50, 0));
    }

    @Test
    void starte_istAusstehendMitNummerBetragUndEigenerId() {
        // act
        var zahlungsvorgang = zahlungsvorgang();

        // assert
        assertThat(zahlungsvorgang.getId()).isNotNull();
        assertThat(zahlungsvorgang.getAnlauf()).isEqualTo(1);
        assertThat(zahlungsvorgang.getBetrag()).isEqualTo(Geldbetrag.euro(50, 0));
        assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Ausstehend);
        assertThat(zahlungsvorgang.istOffen()).isTrue();
        assertThat(zahlungsvorgang.istEingegangen()).isFalse();
    }

    @Test
    void starte_vergibtJedemZahlungsvorgangEineEigeneId() {
        assertThat(zahlungsvorgang().getId()).isNotEqualTo(zahlungsvorgang().getId());
    }

    @Test
    void zahlungEingegangen_ausAusstehend_wirdEingegangen() {
        // arrange
        var zahlungsvorgang = zahlungsvorgang();

        // act
        zahlungsvorgang.zahlungEingegangen();

        // assert
        assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Eingegangen);
        assertThat(zahlungsvorgang.istEingegangen()).isTrue();
        assertThat(zahlungsvorgang.istOffen()).isFalse();
    }

    @Test
    void zahlungAbgebrochen_ausAusstehend_wirdAbgebrochen() {
        // arrange
        var zahlungsvorgang = zahlungsvorgang();

        // act
        zahlungsvorgang.zahlungAbgebrochen();

        // assert
        assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Abgebrochen);
        assertThat(zahlungsvorgang.istOffen()).isFalse();
    }

    @Test
    void zahlungEingegangen_bereitsAbgebrochen_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlungsvorgang = zahlungsvorgang();
        zahlungsvorgang.zahlungAbgebrochen();

        // act / assert
        assertThatThrownBy(zahlungsvorgang::zahlungEingegangen)
                .isInstanceOf(GeschaeftsregelVerletzt.class)
                .hasMessageContaining("Nur ausstehende Zahlungsvorgänge");
    }

    @Test
    void zahlungAbgebrochen_bereitsEingegangen_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlungsvorgang = zahlungsvorgang();
        zahlungsvorgang.zahlungEingegangen();

        // act / assert
        assertThatThrownBy(zahlungsvorgang::zahlungAbgebrochen)
                .isInstanceOf(GeschaeftsregelVerletzt.class);
    }

    @Test
    void zahlungEingegangen_zweimal_wirftGeschaeftsregelVerletzt() {
        // arrange
        var zahlungsvorgang = zahlungsvorgang();
        zahlungsvorgang.zahlungEingegangen();

        // act / assert
        assertThatThrownBy(zahlungsvorgang::zahlungEingegangen)
                .isInstanceOf(GeschaeftsregelVerletzt.class);
    }
}
