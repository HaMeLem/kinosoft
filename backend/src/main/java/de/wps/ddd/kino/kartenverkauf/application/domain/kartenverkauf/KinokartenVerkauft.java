package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import org.jmolecules.event.annotation.DomainEvent;

import java.util.List;

@DomainEvent
public record KinokartenVerkauft(Auftragsnummer auftragsnummer, Vorstellung vorstellung,
                                 List<Kinokarte> kinokarten) {
}
