package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record KinokarteId(UUID wert) {
    public static KinokarteId neu() {
        return new KinokarteId(UUID.randomUUID());
    }
}
