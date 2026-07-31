package de.wps.ddd.kino.kartenverkauf.application.ports.primary;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungEingegangen;

@ApplicationService
public interface VerarbeiteZahlungseingang {
    void fuer(ZahlungEingegangen zahlungEingegangen);
}
