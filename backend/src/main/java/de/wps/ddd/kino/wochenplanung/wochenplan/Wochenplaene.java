package de.wps.ddd.kino.wochenplanung.wochenplan;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface Wochenplaene {

    Optional<Wochenplan> finde(Kalenderwoche kalenderwoche);

    void speichere(Wochenplan wochenplan);
}
