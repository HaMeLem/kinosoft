package de.wps.ddd.kino.filmvorfuehrung.domain.filmvorstellung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.UUID;

@ValueObject
public record VorstellungId(UUID uuid) {

    public static VorstellungId neu() {
        return new VorstellungId(UUID.randomUUID());
    }
}