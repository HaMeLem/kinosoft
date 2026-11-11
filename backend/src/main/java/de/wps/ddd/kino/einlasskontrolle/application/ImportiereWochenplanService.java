package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.einlasskontrolle.domain.*;
import de.wps.ddd.kino.wochenplanung.Wochenplanauskunft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Importiert die Filmvorstellungen eines Wochenplans in die Einlasskontrolle.
 * Wird vom WochenplanErstelltEventListener mit Jahr und KW des Wochenplans aufgerufen.
*/
@Service
@ApplicationService
@RequiredArgsConstructor
public class ImportiereWochenplanService {

    private final Wochenplanauskunft wochenplanauskunft;
    private final FilmvorstellungenRepository filmvorstellungenRepository;

    @Transactional
    public void importiere(int jahr, int kalenderwoche) {
        // Bei der Schnittstelle der Wochenplanung den vollständigen Wochenplan holen
        var wochenplanDto = wochenplanauskunft.holeWochenplan(jahr, kalenderwoche);

        // Alle Filmvorstellungen als eigene fachliche Objekte der Einlasskontrolle erstellen
        for (var vorstellung : wochenplanDto.vorstellungen()){
             var filmvorstellung = new Filmvorstellung(
                    new VorstellungsId(vorstellung.id()),
                    new GeplanteStartzeit(vorstellung.geplanterStart()),
                    new GeplanteEndzeit(vorstellung.geplantesEnde())
            );
             // Die Filmvorstellung zum Speichern an die Schnittstelle Wochenplan weitergeben.
            filmvorstellungenRepository.fuegeVorstellungHinzu(filmvorstellung);
        }
    }
}
