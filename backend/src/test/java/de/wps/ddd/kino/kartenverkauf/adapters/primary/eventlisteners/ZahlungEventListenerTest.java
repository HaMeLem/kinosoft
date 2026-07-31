package de.wps.ddd.kino.kartenverkauf.adapters.primary.eventlisteners;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungAbgebrochen;
import de.wps.ddd.kino.kartenverkauf.application.domain.zahlung.ZahlungEingegangen;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungsabbruch;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.VerarbeiteZahlungseingang;
import de.wps.ddd.kino.zahlung.ZahlungAbgebrochenDto;
import de.wps.ddd.kino.zahlung.ZahlungEingegangenDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ZahlungEventListenerTest {

    private final VerarbeiteZahlungseingang verarbeiteZahlungseingang = mock(VerarbeiteZahlungseingang.class);
    private final VerarbeiteZahlungsabbruch verarbeiteZahlungsabbruch = mock(VerarbeiteZahlungsabbruch.class);
    private final ZahlungEventListener listener =
            new ZahlungEventListener(verarbeiteZahlungseingang, verarbeiteZahlungsabbruch);

    private final UUID referenz = UUID.randomUUID();

    @Test
    void verarbeite_zahlungEingegangenDto_uebergibtDomainEventMitAuftragsnummer() {
        // act
        listener.verarbeite(new ZahlungEingegangenDto(referenz));

        // assert
        verify(verarbeiteZahlungseingang).fuer(new ZahlungEingegangen(new ZahlungsvorgangId(referenz)));
    }

    @Test
    void verarbeite_zahlungAbgebrochenDto_uebergibtDomainEventMitAuftragsnummer() {
        // act
        listener.verarbeite(new ZahlungAbgebrochenDto(referenz));

        // assert
        verify(verarbeiteZahlungsabbruch).fuer(new ZahlungAbgebrochen(new ZahlungsvorgangId(referenz)));
    }
}
