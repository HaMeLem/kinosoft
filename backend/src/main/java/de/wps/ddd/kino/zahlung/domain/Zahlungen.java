package de.wps.ddd.kino.zahlung.domain;

import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface Zahlungen {

    void speichere(Zahlung zahlung);

    Zahlung hole(Zahlungsreferenz referenz);
}
