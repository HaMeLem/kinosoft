package de.wps.ddd.kino.einlasskontrolle.domain;

import lombok.Getter;

import java.time.LocalDateTime;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * Repräsentiert eine Filmvorstellung innerhalb der Einlasskontrolle.
 * Übernimmt die Vorstellungs-ID und die geplanten Zeiten aus der Wochenplanung.
 * Der Einlassvorlauf wird durch die Einlasskontrolle selbst festgelegt.
 */
@AggregateRoot
@Getter
public class Filmvorstellung
{
    @Identity
    private final VorstellungsId id;
    private final GeplanteStartzeit start;
    private final GeplanteEndzeit ende;
    private final EinlassVorlauf einlassVorlauf;

    public Filmvorstellung(
            VorstellungsId id,
            GeplanteStartzeit start,
            GeplanteEndzeit ende
    )
    {
        this.id = id;
        this.start = start;
        this.ende = ende;
        this.einlassVorlauf = EinlassVorlauf.EINLASS_VORLAUF_MINUTEN;
    }

    public LocalDateTime einlassBeginn() {
    return start.start().minusMinutes(einlassVorlauf.wert());
    }

    public boolean istEinlassNochNichtOffen(LocalDateTime jetzt) {
    return jetzt.isBefore(einlassBeginn());
    }

    public boolean istEinlassVorbei(LocalDateTime jetzt) {
    return jetzt.isAfter(ende.ende());
    }
}
