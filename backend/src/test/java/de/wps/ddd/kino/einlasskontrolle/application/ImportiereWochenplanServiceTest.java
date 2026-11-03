package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.FilmvorstellungenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import de.wps.ddd.kino.wochenplanung.WochenplanDto;
import de.wps.ddd.kino.wochenplanung.Wochenplanauskunft;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ImportiereWochenplanServiceTest {

    /**
     * Happy-Path Test
     */
    @Test
    void erstelleZweiFilmvorstellungen() {
        // Zwei Testvorstellungen erstellen
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();

        var start1 = LocalDateTime.of(2026, 11, 2, 15, 0);
        var ende1 = LocalDateTime.of(2026, 11, 2, 17, 0);

        var start2 = LocalDateTime.of(2026, 11, 3, 20, 0);
        var ende2 = LocalDateTime.of(2026, 11, 3, 22, 30);

        var vorstellung1 = new WochenplanDto.VorstellungDto(id1, "Film 1", "Saal 1", "Comedy", start1, ende1);
        var vorstellung2 = new WochenplanDto.VorstellungDto(id2, "Film 2", "Saal 2", "Thriller", start2, ende2);

        // Testwochenplan erstellen
        var testWochenplan = new WochenplanDto(List.of(), List.of(vorstellung1, vorstellung2));

        Wochenplanauskunft auskunft = (
                (jahr, kalenderwoche) -> { assertEquals(2026, jahr);
        assertEquals(45, kalenderwoche);
        return testWochenplan;
        });

        // Vereinfachte Speicherung für den Test
        var gespeicherteVorstellungen =
                new HashMap<VorstellungsId, Filmvorstellung>();

        FilmvorstellungenRepository repository = new FilmvorstellungenRepository() {

            @Override
            public void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung) {
                gespeicherteVorstellungen.put(
                        filmvorstellung.getId(), filmvorstellung);
            }

            @Override
            public Optional<Filmvorstellung> holeVorstellung(VorstellungsId id) {
                return Optional.ofNullable(gespeicherteVorstellungen.get(id));
            }


        };

        // ImportService erstellen
        var service = new ImportiereWochenplanService(auskunft, repository);

        // Import ausführen
        service.importiere(2026, 45);

        // Überprüfen, ob beide Vorstellungen importiert wurden
        assertEquals(2, gespeicherteVorstellungen.size());

        // Erste Vorstellung überprüfen
        var importiert1 = gespeicherteVorstellungen.get(new VorstellungsId(id1));

        assertNotNull(importiert1);
        assertEquals(start1, importiert1.getStart().start());
        assertEquals(ende1, importiert1.getEnde().ende());

        // Zweite Vorstellung überprüfen
        var importiert2 = gespeicherteVorstellungen.get(new VorstellungsId(id2));

        assertNotNull(importiert2);
        assertEquals(start2, importiert2.getStart().start());
        assertEquals(ende2, importiert2.getEnde().ende());
    }

}
