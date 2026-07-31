package de.wps.ddd.kino.zahlung.domain;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Zahlung {

    @Identity
    private final Zahlungsreferenz referenz;
    private final Betrag betrag;
    private Zahlungsstatus status;

    public static Zahlung fuer(Zahlungsreferenz referenz, Betrag betrag) {
        return new Zahlung(referenz, betrag, Zahlungsstatus.Offen);
    }

    public Zahlung eingegangen() {
        GeschaeftsregelVerletzt.wenn(status != Zahlungsstatus.Offen, "Nur offene Zahlungen können eingehen.");
        this.status = Zahlungsstatus.Eingegangen;
        return this;
    }

    public Zahlung abgebrochen() {
        GeschaeftsregelVerletzt.wenn(status != Zahlungsstatus.Offen, "Nur offene Zahlungen können abgebrochen werden.");
        this.status = Zahlungsstatus.Abgebrochen;
        return this;
    }
}
