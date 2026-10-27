package de.wps.ddd.kino.einlasskontrolle.domain;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.UUID;


@ValueObject
public record VorstellungsId(UUID id)
{
}
