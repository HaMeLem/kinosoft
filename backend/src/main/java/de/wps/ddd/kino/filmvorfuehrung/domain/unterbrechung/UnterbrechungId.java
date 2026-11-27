package de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung;

import java.util.UUID;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record UnterbrechungId(UUID uuid) {

    public static UnterbrechungId neu() {
        return new UnterbrechungId(UUID.randomUUID());
    }
}