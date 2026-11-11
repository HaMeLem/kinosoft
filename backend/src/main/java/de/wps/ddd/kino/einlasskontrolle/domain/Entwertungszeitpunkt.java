package de.wps.ddd.kino.einlasskontrolle.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

@ValueObject
public record Entwertungszeitpunkt(LocalDateTime wert)
{
}
