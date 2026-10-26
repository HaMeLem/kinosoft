package de.wps.ddd.kino.einlass.domain;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.UUID;


@ValueObject
public record VorstellungsId(UUID id)
{
}
