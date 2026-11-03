package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.Saal;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ImportiereKinokartenVerkauftServiceTest
{

    @Test
    /* Test prüft, ob die importiere Methode korrekt läuft// HAPPY PATH
     */
    void importiere_speichertKartenMitDenUebergebenenDaten() {
        // given
        //nehmen das Fake Repo
        RecordingKinokartenRepository repository = new RecordingKinokartenRepository();
        // testen aber den echten Service
        ImportiereKinokartenVerkauftService service =
                new ImportiereKinokartenVerkauftService(repository);
        UUID vorstellungId = UUID.randomUUID();
        UUID ersteKartenId = UUID.randomUUID();
        UUID zweiteKartenId = UUID.randomUUID();
        VerkaufteKinokarten verkauf = new VerkaufteKinokarten(
                vorstellungId,
                "kleiner Saal",
                List.of(
                        new VerkaufteKinokarten.Karte(ersteKartenId, 3, 5),
                        new VerkaufteKinokarten.Karte(zweiteKartenId, 3, 6)));

        // when
        // übergeben dem Servie die verkauften Kinokarten
        service.importiere(verkauf);

        // then
        //wir erwarten, dass das Objekt zwei Kinokarten enthält
        assertThat(repository.gespeicherteKarten).hasSize(2);
        // holen uns die erste Kinokarte
        Kinokarte ersteKarte = repository.gespeicherteKarten.get(0);
        // gleichen ID ab mit dem , was in ersteKartenid gespeichert ist
        assertThat(ersteKarte.getId().id()).isEqualTo(ersteKartenId);
        // prüfen die Reihe
        assertThat(ersteKarte.getReihe().wert()).isEqualTo(3);
        //prüfen den Sitzplatz
        assertThat(ersteKarte.getSitzplatz().wert()).isEqualTo(5);
        //prüfen den Saal auf "kleiner Saal"
        assertThat(ersteKarte.getSaal()).isEqualTo(Saal.KLEINER_SAAL);
        // prüfen die Vostellungsid
        assertThat(ersteKarte.getFilmvorstellung().id()).isEqualTo(vorstellungId);
        // holen uns die zweite, im Fake Repo (aka Liste) gespeicherte Kinokarte
        Kinokarte zweiteKarte = repository.gespeicherteKarten.get(1);
        assertThat(zweiteKarte.getId().id()).isEqualTo(zweiteKartenId);
        assertThat(zweiteKarte.getSitzplatz().wert()).isEqualTo(6);

        // AUFRÄUMEN: nach Nutzung des Fake Repo wird die Liste wieder geleert (eigentl. nicht nötig, da in jedem Test neues Fake Repo, mit neuer Liste genutzt wird
        repository.gespeicherteKarten.clear();
    }

    @Test
    void importiere_verwendetDenGrossenSaal() {
        // given
        // holen uns wieder das Fake Repo
        RecordingKinokartenRepository repository = new RecordingKinokartenRepository();
        ImportiereKinokartenVerkauftService service =
                new ImportiereKinokartenVerkauftService(repository);
        VerkaufteKinokarten verkauf = new VerkaufteKinokarten(
                UUID.randomUUID(),
                "großer Saal",
                List.of(new VerkaufteKinokarten.Karte(UUID.randomUUID(), 1, 1)));

        // when
        // rufen den Service auf mit gemockter verkauter Kinokarte
        service.importiere(verkauf);

        // then
        // erwarten, dass eine Karte gespeichert wurde
        assertThat(repository.gespeicherteKarten).hasSize(1);
        //prüfen, dass es "großer Saal" ist
        assertThat(repository.gespeicherteKarten.get(0).getSaal())
                .isEqualTo(Saal.GROSSER_SAAL);

        // nach Nutzung des Fake Repo wird die Liste wieder geleert (eigentl. nicht nötig, da in jedem Test neues Fake Repo, mit neuer Liste genutzt wird
        repository.gespeicherteKarten.clear();
    }

    @Test
    /*
    Test prüft, dass im Fehlerfall nicht versucht wird zu speichern
     */
    void importiere_wirftBeiUnbekanntemSaalUndSpeichertKeineKarten() {
        // given
        //schnappen uns wieder das Fake Repo
        RecordingKinokartenRepository repository = new RecordingKinokartenRepository();
        // aber den echten Service
        ImportiereKinokartenVerkauftService service =
                new ImportiereKinokartenVerkauftService(repository);
        // erstellen uns eine verkaufte Kinokarte mit ungültigem Saalnamen
        VerkaufteKinokarten verkauf = new VerkaufteKinokarten(
                UUID.randomUUID(),
                "unbekannter Saal",
                List.of(new VerkaufteKinokarten.Karte(UUID.randomUUID(), 1, 1)));

        // when / then
        //prüft, dass die Exception im Fehlerfall (hier unbek. Saalname) wie gewünscht geworfen wird
        assertThatThrownBy(() -> service.importiere(verkauf))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unbekannter Saalname: unbekannter Saal");
        // prüfen, dass nicht gespeichert wird
        assertThat(repository.gespeicherteKarten).isEmpty();

        // nach Nutzung des Fake Repo wird die Liste wieder geleert (eigentl. nicht nötig, da in jedem Test neues Fake Repo, mit neuer Liste genutzt wird
        repository.gespeicherteKarten.clear();
    }

    //Erstellen Fake Repo, dass die Karten vorübergehend in Liste speichert, um DB Interaktion zu umgehen

    private static class RecordingKinokartenRepository implements KinokartenRepository {
        //Fake Repo speichert die verkauften Karten in einer Liste
        private final List<Kinokarte> gespeicherteKarten = new ArrayList<>();

        @Override
        public Kinokarte hole(KinokartenId id) {
            throw new UnsupportedOperationException("hole() wird in diesen Tests nicht benötigt");
        }

        @Override
        public void speichere(Kinokarte kinokarte) {
            gespeicherteKarten.add(kinokarte);
        }
    }
}
