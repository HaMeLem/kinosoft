package de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.UUID;

@ValueObject
public record VorfuehrungId(UUID uuid) {

    public static VorfuehrungId neu() {
        return new VorfuehrungId(UUID.randomUUID());
    }
}