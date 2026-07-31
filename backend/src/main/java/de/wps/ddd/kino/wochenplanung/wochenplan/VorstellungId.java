package de.wps.ddd.kino.wochenplanung.wochenplan;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record VorstellungId(UUID wert) {
}
