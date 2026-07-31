package de.wps.ddd.kino.filmauswahl.service;

import de.wps.ddd.kino.wochenplanung.WochenplanErstelltDto;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service("filmauswahlWochenplanEventListener")
@RequiredArgsConstructor
public class WochenplanEventListener {

    private final WochenplanImportService wochenplanImportService;

    @ApplicationModuleListener
    public void verarbeite(WochenplanErstelltDto dto) {
        wochenplanImportService.importiere(dto.jahr(), dto.kalenderwoche());
    }
}
