package de.wps.ddd.kino.kartenverkauf.application.ports.primary;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungAbgebrochen;

@ApplicationService
public interface VerarbeiteZahlungsabbruch {
    void fuer(ZahlungAbgebrochen zahlungAbgebrochen);
}
