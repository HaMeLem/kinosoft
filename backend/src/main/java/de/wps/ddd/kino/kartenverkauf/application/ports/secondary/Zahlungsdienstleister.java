package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.common.architecture.Gateway;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;

@Gateway
public interface Zahlungsdienstleister {

    void starteZahlung(ZahlungsvorgangId zahlungsvorgangId, Geldbetrag betrag);
}
