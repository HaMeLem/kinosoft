package de.wps.ddd.kino.wochenplanung;

import de.wps.ddd.kino.wochenplanung.application.Wochenplanauskunft;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Altersfreigabe;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Film;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmkatalog;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmtitel;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Geldbetrag;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Verleih;
import de.wps.ddd.kino.wochenplanung.saalverwaltung.Saalname;
import de.wps.ddd.kino.wochenplanung.wochenplan.Vorstellung;
import de.wps.ddd.kino.wochenplanung.wochenplan.Kalenderwoche;
import de.wps.ddd.kino.wochenplanung.wochenplan.Vorstellungskategorie;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplan;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplaene;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class WochenplanungIntegrationTest {

    @Autowired
    private Wochenplanauskunft wochenplanauskunft;

    @Autowired
    private Filmkatalog filmkatalog;

    @Autowired
    private Wochenplaene wochenplaene;

    @Test
    void stelltWochenplanBereit() {
        // arrange — Katalog und Wochenplan seeden (der Saal wird nur über seinen Namen referenziert)
        var titel = new Filmtitel("Star Boars");
        filmkatalog.speichere(new Film(titel, 125, "assets/Star_Boars.jpeg", "Beschreibung", "Sci-Fi, Comedy",
                "Luke Stywalker", "George Laxus", "deutsch", Altersfreigabe.FSK12,
                new Verleih("Galaktik Filmverleih", Geldbetrag.euro(1500, 0), LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 31))));

        var wochenplan = Wochenplan.fuer(new Kalenderwoche(2025, 12));
        wochenplan.planeVorstellung(Vorstellung.plane(filmkatalog.finde(titel).orElseThrow(),
                new Saalname("großer Saal"), Vorstellungskategorie.Standard, LocalDateTime.of(2025, 3, 17, 14, 30)));
        wochenplaene.speichere(wochenplan);
        var erwarteteId = wochenplan.getVorstellungen().get(0).id().wert();

        // act — die neutrale, für beide Consumer gemeinsame Sicht
        var sicht = wochenplanauskunft.holeWochenplan(2025, 12);

        // assert — Filme: nur Stammdaten (fsk als int), keine internen Details wie Verleihgebühr
        assertThat(sicht.filme()).singleElement().satisfies(f -> {
            assertThat(f.titel()).isEqualTo("Star Boars");
            assertThat(f.fsk()).isEqualTo(12);
        });

        // assert — Vorstellungen: Bezug auf Film/Saal per Name, nur der Vorstellungsbeginn (nicht die
        // planungsinternen Zeiten). Die Bestuhlung gehört nicht zur Wochenplanung.
        assertThat(sicht.vorstellungen()).singleElement().satisfies(v -> {
            assertThat(v.id()).isEqualTo(erwarteteId);
            assertThat(v.filmTitel()).isEqualTo("Star Boars");
            assertThat(v.saalName()).isEqualTo("großer Saal");
            assertThat(v.kategorie()).isEqualTo("Standard");
            assertThat(v.beginn()).isEqualTo(LocalDateTime.of(2025, 3, 17, 14, 30));
        });
    }
}
