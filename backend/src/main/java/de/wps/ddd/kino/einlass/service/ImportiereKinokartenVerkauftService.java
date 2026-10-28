package de.wps.ddd.kino.einlass.service;
import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.einlass.domain.Kinokarte;
import de.wps.ddd.kino.einlass.domain.KinokartenId;
import de.wps.ddd.kino.einlass.domain.KinokartenRepository;
import de.wps.ddd.kino.einlass.domain.Reihe;
import de.wps.ddd.kino.einlass.domain.Saal;
import de.wps.ddd.kino.einlass.domain.Sitzplatz;
import de.wps.ddd.kino.einlass.domain.VorstellungsId;
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

    @Transactional
    public void importiere(VerkaufteKinokarten verkauf) {
        /*wandelt die VorstellungsId aus einer UUID in das Value Object VorstellungsId um*/
        var vorstellungId = new VorstellungsId(verkauf.vorstellungId());
        var saal = mapSaal(verkauf.saal());
/* für jede verkaufte Karte wird ein Kinokarte-Aggregat erstellt, dabei werden 
KartenId, Reihe, Sitzplatz in Value Objekte umgewandelt */
        for (var karte : verkauf.karten()) {
            var kinokarte = new Kinokarte(
                    new KinokartenId(karte.kartenId()),
                    new Reihe(karte.reihe()),
                    new Sitzplatz(karte.platz()),
                    saal,
                    vorstellungId);
/* jedes Aggregat wird an kinokartenRepository übergeben, da noch keine Implementierung von kinokartenRepository existiert, passiert das
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