package de.wps.ddd.kino.kartenverkauf.application.ports.primary;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;

@ApplicationService
public interface HoleZahlungsstatus {
    Zahlungsstatus fuer(Auftragsnummer auftragsnummer);
}
