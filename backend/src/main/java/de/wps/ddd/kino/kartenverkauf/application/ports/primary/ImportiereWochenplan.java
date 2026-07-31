package de.wps.ddd.kino.kartenverkauf.application.ports.primary;

import de.wps.ddd.kino.common.architecture.ApplicationService;

@ApplicationService
public interface ImportiereWochenplan {

    void fuer(int jahr, int kalenderwoche);
}
