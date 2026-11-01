package de.wps.ddd.kino.wochenplanung.wochenplan;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

/**
 * Planungsinterne Zeiten einer Vorstellung (Einlass, Vorstellungsbeginn, Filmende). Nach außen
 * exponiert die Wochenplan-Sicht nur den {@link #geplanterStart()}.
 */
@ValueObject
public record Vorstellungszeiten(LocalDateTime einlass, LocalDateTime geplanterStart, LocalDateTime geplantesEnde) {

    private static final int EINLASS_VORLAUF_MINUTEN = 30;
    private static final int WERBUNG_MINUTEN = 15;

    public static Vorstellungszeiten plane(LocalDateTime geplanterStart, int laufzeitMinuten) {
        var einlass = geplanterStart.minusMinutes(EINLASS_VORLAUF_MINUTEN);
        var geplantesEnde = geplanterStart.plusMinutes((long) WERBUNG_MINUTEN + laufzeitMinuten);
        return new Vorstellungszeiten(einlass, geplanterStart, geplantesEnde);
    }
}
