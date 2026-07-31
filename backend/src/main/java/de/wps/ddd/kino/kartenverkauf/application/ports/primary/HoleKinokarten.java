package de.wps.ddd.kino.kartenverkauf.application.ports.primary;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;

import java.util.List;

public interface HoleKinokarten {
    List<Kinokarte> fuer(Auftragsnummer auftragsnummer);
}
