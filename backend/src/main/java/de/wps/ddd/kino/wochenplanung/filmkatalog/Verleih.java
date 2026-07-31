package de.wps.ddd.kino.wochenplanung.filmkatalog;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDate;

/**
 * Planungsinterne Verleih-Daten eines Films (Verleiher, Verleihgebühr, Lizenzfrist). Diese Details
 * bleiben im Kontext der Wochenplanung und landen nicht in den nach außen exponierten Sicht-DTOs.
 */
@ValueObject
public record Verleih(String verleiher, Geldbetrag verleihgebuehr, LocalDate lizenzbeginn, LocalDate lizenzende) {
}
