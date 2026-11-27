package de.wps.ddd.kino.filmvorfuehrung.infrastructure.messaging;


import de.wps.ddd.kino.filmvorfuehrung.application.ImportiereWochenplanService;
import de.wps.ddd.kino.wochenplanung.WochenplanErstelltDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service("filmvorfuehrungWochenplanErstelltEventListener")
@RequiredArgsConstructor
public class WochenplanErstelltEventListener {
    //zusätzliche Absicherung, dass der korrekte Service genutzt wird
    @Qualifier("filmvorfuehrungImportiereWochenplanService")
    private final ImportiereWochenplanService importiereWochenplanService;

    @ApplicationModuleListener
    public void verarbeite(WochenplanErstelltDto dto) {
        importiereWochenplanService.importiere(
                dto.jahr(),
                dto.kalenderwoche());
    }
}
