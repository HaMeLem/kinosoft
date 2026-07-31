package de.wps.ddd.kino.zahlung.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Betrag(long cent) {
}
