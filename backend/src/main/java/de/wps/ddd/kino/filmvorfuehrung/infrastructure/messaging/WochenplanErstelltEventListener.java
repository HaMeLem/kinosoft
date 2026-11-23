package de.wps.ddd.kino.filmvorfuehrung.infrastructure.messaging;


import de.wps.ddd.kino.filmvorfuehrung.application.ImportiereWochenplanService;
import de.wps.ddd.kino.wochenplanung.WochenplanErstelltDto;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

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
