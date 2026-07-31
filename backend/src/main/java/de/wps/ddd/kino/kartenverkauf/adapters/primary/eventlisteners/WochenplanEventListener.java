package de.wps.ddd.kino.kartenverkauf.adapters.primary.eventlisteners;

import de.wps.ddd.kino.kartenverkauf.application.ports.primary.ImportiereWochenplan;
import de.wps.ddd.kino.wochenplanung.WochenplanErstelltDto;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service("kartenverkaufWochenplanEventListener")
@RequiredArgsConstructor
public class WochenplanEventListener {

    private final ImportiereWochenplan importiereWochenplan;

    @ApplicationModuleListener
    public void verarbeite(WochenplanErstelltDto dto) {
        importiereWochenplan.fuer(dto.jahr(), dto.kalenderwoche());
    }
}
