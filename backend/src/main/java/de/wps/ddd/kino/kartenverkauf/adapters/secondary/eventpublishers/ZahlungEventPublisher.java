package de.wps.ddd.kino.kartenverkauf.adapters.secondary.eventpublishers;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Zahlungsdienstleister;
import de.wps.ddd.kino.zahlung.StarteZahlungDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZahlungEventPublisher implements Zahlungsdienstleister {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void starteZahlung(ZahlungsvorgangId zahlungsvorgangId, Geldbetrag betrag) {
        eventPublisher.publishEvent(new StarteZahlungDto(zahlungsvorgangId.wert(), betrag.getBetrag()));
    }
}
