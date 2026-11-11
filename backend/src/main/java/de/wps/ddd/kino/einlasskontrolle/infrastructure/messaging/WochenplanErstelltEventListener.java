package de.wps.ddd.kino.einlasskontrolle.infrastructure.messaging;

import de.wps.ddd.kino.wochenplanung.WochenplanErstelltDto;
import de.wps.ddd.kino.einlasskontrolle.application.ImportiereWochenplanService;

import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

/**
 * Event Listener, der auf das Ereignis WochenplanErstelltDto hört.
 * Übergibt daraufhin Jahr und KW an den ImportService, der die Filmvorstellungen importieren soll.
 */
@Service
@RequiredArgsConstructor
public class WochenplanErstelltEventListener {

    private final ImportiereWochenplanService importiereWochenplanService;

    @ApplicationModuleListener
    public void verarbeite(WochenplanErstelltDto dto) {
        importiereWochenplanService.importiere(
                dto.jahr(),
                dto.kalenderwoche());
    }
}