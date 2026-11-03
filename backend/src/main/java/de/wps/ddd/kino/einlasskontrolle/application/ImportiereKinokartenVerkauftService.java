package de.wps.ddd.kino.einlasskontrolle.application;
import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.Reihe;
import de.wps.ddd.kino.einlasskontrolle.domain.Saal;
import de.wps.ddd.kino.einlasskontrolle.domain.Sitzplatz;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ApplicationService
@Service
@RequiredArgsConstructor

/* EventListener ruft diese Service-Klasse mit einem verkaufteKinokarten-Datensatz auf, 
um die verkauften Kinokarten zu importieren.
*/
public class ImportiereKinokartenVerkauftService {

    private final KinokartenRepository kinokartenRepository;
    // Implementierung von KinoKartenRepo fehlt noch

    @Transactional // alles oder nichts Prinzip
    public void importiere(VerkaufteKinokarten verkauf) {
        //
        VorstellungsId  vorstellungId = new VorstellungsId(verkauf.vorstellungId());
        Saal saal = mapSaal(verkauf.saal());
        /* iterieren durch die Liste

        */
        for (VerkaufteKinokarten.Karte karte : verkauf.karten()) {
            Kinokarte kinokarte = new Kinokarte(
                    new KinokartenId(karte.kartenId()),
                    new Reihe(karte.reihe()),
                    new Sitzplatz(karte.platz()),
                    saal,
                    vorstellungId);
/* jede einzelne Kinokarte wird an kinokartenRepository übergeben, da noch keine Implementierung von kinokartenRepository existiert, passiert das
SPEICHERN AKTUELL NOCH NICHT*/
            kinokartenRepository.speichere(kinokarte);
        }
    }

 /*übersetzt den Saalnamen aus dem Event z.B."kleiner Saal" in
  das passende Einlass-Enum Saal.KLEINER_SAAL*/
    private Saal mapSaal(String saalname) {
        if (saalname.equals("kleiner Saal")) {
            return Saal.KLEINER_SAAL;
        }
        if (saalname.equals("großer Saal")) {
            return Saal.GROSSER_SAAL;
        }
        throw new IllegalArgumentException("Unbekannter Saalname: " + saalname);
    }
}