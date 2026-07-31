package de.wps.ddd.kino.zahlung.persistence;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.zahlung.domain.Betrag;
import de.wps.ddd.kino.zahlung.domain.Zahlung;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZahlungenRepositoryTest {

    private final ZahlungenRepository zahlungen = new ZahlungenRepository();
    private final Zahlungsreferenz referenz = new Zahlungsreferenz(UUID.randomUUID());

    @Test
    void speichereUndHole_liefertGespeicherteZahlung() {
        // arrange
        var zahlung = Zahlung.fuer(referenz, new Betrag(5000));
        zahlungen.speichere(zahlung);

        // act / assert
        assertThat(zahlungen.hole(referenz)).isSameAs(zahlung);
    }

    @Test
    void hole_unbekannteReferenz_wirftRessourceNichtGefunden() {
        // act / assert
        assertThatThrownBy(() -> zahlungen.hole(referenz))
                .isInstanceOf(RessourceNichtGefunden.class)
                .hasMessageContaining("existiert nicht");
    }
}
