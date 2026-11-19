package de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.UUID;

@ValueObject
public record UnterbrechungId(UUID uuid) {
}