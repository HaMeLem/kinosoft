package de.wps.ddd.kino.wochenplanung.filmkatalog;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Kontexteigener Geldbetrag der Wochenplanung (z.B. für Verleihgebühren). Bewusst getrennt vom
 * {@code Geldbetrag} des Kartenverkaufs — jeder Kontext besitzt seine eigenen Typen.
 */
@ValueObject
public record Geldbetrag(long cent) {

    public static Geldbetrag euro(int euro, int cent) {
        return new Geldbetrag(euro * 100L + cent);
    }
}
