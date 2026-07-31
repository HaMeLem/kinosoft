package de.wps.ddd.kino.wochenplanung.wochenplan;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

/**
 * Planungsinterne Zeiten einer Vorstellung (Einlass, Vorstellungsbeginn, Filmende). Nach außen
 * exponiert die Wochenplan-Sicht nur den {@link #vorstellungsbeginn()}.
 */
@ValueObject
public record Vorstellungszeiten(LocalDateTime einlass, LocalDateTime vorstellungsbeginn, LocalDateTime filmende) {

    private static final int EINLASS_VORLAUF_MINUTEN = 20;
    private static final int WERBUNG_MINUTEN = 15;

    public static Vorstellungszeiten plane(LocalDateTime vorstellungsbeginn, int laufzeitMinuten) {
        var einlass = vorstellungsbeginn.minusMinutes(EINLASS_VORLAUF_MINUTEN);
        var filmende = vorstellungsbeginn.plusMinutes((long) WERBUNG_MINUTEN + laufzeitMinuten);
        return new Vorstellungszeiten(einlass, vorstellungsbeginn, filmende);
    }
}
