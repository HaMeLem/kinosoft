package de.wps.ddd.kino.kartenverkauf.application.domain.zahlung;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record ZahlungEingegangen(ZahlungsvorgangId zahlungsvorgangId) {
}
