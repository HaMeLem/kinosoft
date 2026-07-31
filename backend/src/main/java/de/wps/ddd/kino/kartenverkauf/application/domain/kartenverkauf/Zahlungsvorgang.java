package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
@Getter
@AllArgsConstructor
public class Zahlungsvorgang {

    @Identity
    private final ZahlungsvorgangId id;
    /** Ordnungszahl innerhalb des Verkaufsvorgangs, keine Identität. */
    private final int anlauf;
    private final Geldbetrag betrag;
    private Zahlungsstatus status;

    static Zahlungsvorgang starte(int anlauf, Geldbetrag betrag) {
        return new Zahlungsvorgang(ZahlungsvorgangId.neu(), anlauf, betrag, Zahlungsstatus.Ausstehend);
    }

    void zahlungEingegangen() {
        GeschaeftsregelVerletzt.wenn(!istOffen(), "Nur ausstehende Zahlungsvorgänge können eingehen.");
        this.status = Zahlungsstatus.Eingegangen;
    }

    void zahlungAbgebrochen() {
        GeschaeftsregelVerletzt.wenn(!istOffen(), "Nur ausstehende Zahlungsvorgänge können abgebrochen werden.");
        this.status = Zahlungsstatus.Abgebrochen;
    }

    public boolean istOffen() {
        return this.status == Zahlungsstatus.Ausstehend;
    }

    public boolean istEingegangen() {
        return this.status == Zahlungsstatus.Eingegangen;
    }
}
