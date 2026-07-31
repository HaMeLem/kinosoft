package de.wps.ddd.kino.kartenverkauf.adapters.secondary.eventpublishers;

import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.WochenplanUebernommen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Wochenplanmeldungen;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WochenplanUebernommenPublisher implements Wochenplanmeldungen {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void melde(WochenplanUebernommen wochenplanUebernommen) {
        eventPublisher.publishEvent(wochenplanUebernommen);
    }
}
