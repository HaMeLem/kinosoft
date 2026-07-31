package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.common.architecture.Gateway;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.WochenplanUebernommen;

@Gateway
public interface Wochenplanmeldungen {

    void melde(WochenplanUebernommen wochenplanUebernommen);
}
