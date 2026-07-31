package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

/** Zugleich die Referenz, unter der der Zahlungsdienstleister den Zahlungsvorgang führt. */
@ValueObject
public record ZahlungsvorgangId(UUID wert) {

    public static ZahlungsvorgangId neu() {
        return new ZahlungsvorgangId(UUID.randomUUID());
    }
}
