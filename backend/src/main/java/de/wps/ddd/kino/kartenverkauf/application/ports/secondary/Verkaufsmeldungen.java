package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.common.architecture.Gateway;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KinokartenVerkauft;

@Gateway
public interface Verkaufsmeldungen {

    void melde(KinokartenVerkauft kinokartenVerkauft);
}
