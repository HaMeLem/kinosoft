package de.wps.ddd.kino.zahlung.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record Zahlungsreferenz(UUID wert) {
}
