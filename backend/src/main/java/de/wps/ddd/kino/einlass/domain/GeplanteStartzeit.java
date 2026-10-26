package de.wps.ddd.kino.einlass.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;

@ValueObject
public record GeplanteStartzeit(LocalDateTime start)
{
}
