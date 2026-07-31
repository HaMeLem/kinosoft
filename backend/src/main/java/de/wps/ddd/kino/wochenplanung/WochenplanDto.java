package de.wps.ddd.kino.wochenplanung;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Neutrale, für beide Consumer gemeinsame Sicht auf einen Wochenplan. Enthält nur exponierbare
 * Daten (Film-Stammdaten, Vorstellungen mit Vorstellungsbeginn und Saalname) — keine
 * planungsinternen Details wie Verleihgebühren, Lizenzfristen oder Einlass-/Sperrzeiten. Die
 * Bestuhlung der Säle gehört nicht zur Wochenplanung; sie ist Sache des jeweiligen Consumers
 * (der Kartenverkauf kennt sie selbst).
 * <p>
 * Filmauswahl und Kartenverkauf übersetzen dieses DTO in ihren jeweils eigenen Kontext
 * (Anti-Corruption-Layer).
 */
public record WochenplanDto(List<FilmDto> filme, List<VorstellungDto> vorstellungen) {

    public record FilmDto(
            String titel,
            int laufzeit,
            String posterUrl,
            int fsk,
            String beschreibung,
            String genre,
            String hauptdarsteller,
            String regie,
            String sprache) {
    }

    public record VorstellungDto(UUID id, String filmTitel, String saalName, String kategorie, LocalDateTime beginn) {
    }
}
