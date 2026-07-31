package de.wps.ddd.kino.zahlung.persistence;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.zahlung.domain.Zahlung;
import de.wps.ddd.kino.zahlung.domain.Zahlungen;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ZahlungenRepository implements Zahlungen {

    private final ConcurrentHashMap<Zahlungsreferenz, Zahlung> zahlungen = new ConcurrentHashMap<>();

    @Override
    public void speichere(Zahlung zahlung) {
        zahlungen.put(zahlung.getReferenz(), zahlung);
    }

    @Override
    public Zahlung hole(Zahlungsreferenz referenz) {
        var zahlung = zahlungen.get(referenz);
        RessourceNichtGefunden.wenn(zahlung == null, "Zahlung " + referenz.wert() + " existiert nicht");
        return zahlung;
    }
}
