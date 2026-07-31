package de.wps.ddd.kino.wochenplanung.wochenplan;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

/**
 * Wochenplan als Aggregat, identifiziert über die {@link Kalenderwoche}. Enthält die eingeplanten
 * Vorstellungen einer Woche.
 */
@AggregateRoot
public class Wochenplan {

    @Identity
    private final Kalenderwoche kalenderwoche;
    private final List<Vorstellung> vorstellungen;

    private Wochenplan(Kalenderwoche kalenderwoche) {
        this.kalenderwoche = kalenderwoche;
        this.vorstellungen = new ArrayList<>();
    }

    public static Wochenplan fuer(Kalenderwoche kalenderwoche) {
        return new Wochenplan(kalenderwoche);
    }

    public void planeVorstellung(Vorstellung vorstellung) {
        vorstellungen.add(vorstellung);
    }

    public Kalenderwoche getKalenderwoche() {
        return kalenderwoche;
    }

    public List<Vorstellung> getVorstellungen() {
        return List.copyOf(vorstellungen);
    }
}
