package de.wps.ddd.kino.kartenverkauf.adapters.primary.eventlisteners;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungAbgebrochen;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungEingegangen;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungsabbruch;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungseingang;
import de.wps.ddd.kino.zahlung.ZahlungAbgebrochenDto;
import de.wps.ddd.kino.zahlung.ZahlungEingegangenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZahlungEventListener {

    private final VerarbeiteZahlungseingang verarbeiteZahlungseingang;

    private final VerarbeiteZahlungsabbruch verarbeiteZahlungsabbruch;

    @ApplicationModuleListener
    public void verarbeite(ZahlungEingegangenDto dto) {
        verarbeiteZahlungseingang.fuer(new ZahlungEingegangen(new ZahlungsvorgangId(dto.referenz())));
    }

    @ApplicationModuleListener
    public void verarbeite(ZahlungAbgebrochenDto dto) {
        verarbeiteZahlungsabbruch.fuer(new ZahlungAbgebrochen(new ZahlungsvorgangId(dto.referenz())));
    }
}
