package de.wps.ddd.kino;

import de.wps.ddd.kino.filmauswahl.service.ProgrammService;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platz;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Basisklasse für Integrationstests, die den beim Start via {@code WochenplanFixture} veröffentlichten
 * Wochenplan benötigen. Da Filmauswahl und Kartenverkauf ihre Sichten asynchron
 * ({@code @ApplicationModuleListener} nach Commit) beziehen, wartet die Basisklasse (Awaitility) auf
 * die fertige Bestückung, bevor der eigentliche Test läuft.
 * <p>
 * Über {@link FesteZeitTestConfig} ist die Anwendungsuhr auf den Bezugstag 2025-03-19 (KW 12/2025)
 * gepinnt, sodass die {@code WochenplanFixture} deterministisch denselben Fahrplan erzeugt und die
 * Datums-Literale der Subklassen stabil bleiben.
 */
@Import(FesteZeitTestConfig.class)
public abstract class IntegrationTestMitWochenplan {

    private static final int ERWARTETE_ANZAHL_VORSTELLUNGEN = 24;
    private static final LocalDate TAG_MIT_VORSTELLUNGEN = LocalDate.of(2025, 3, 19);

    @Autowired
    private AktuelleVorstellungen aktuelleVorstellungen;

    @Autowired
    private SaalplanStapel saalplanStapel;

    @Autowired
    private ProgrammService programmService;

    @BeforeEach
    void warteAufWochenplanUebernahme() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofMillis(200))
                .until(this::wochenplanUebernommen);
    }

    private boolean wochenplanUebernommen() {
        var vorstellungen = aktuelleVorstellungen.alleVorstellungen();
        return vorstellungen.size() >= ERWARTETE_ANZAHL_VORSTELLUNGEN
                && !programmService.holeVorstellungenFuerTag(TAG_MIT_VORSTELLUNGEN).isEmpty()
                && belegungAngewandt(vorstellungen.get(0).getId());
    }

    /**
     * Signalisiert, dass die (async) SaalplanBelegungFixture fertig ist: sie committet ihre Belegung
     * atomar, ein verkaufter Platz auf einem Saalplan bedeutet also „Belegung vollständig".
     */
    private boolean belegungAngewandt(VorstellungId vorstellungId) {
        return saalplanStapel.holeSaalplan(vorstellungId).getPlaetze().values().stream()
                .flatMap(reihe -> reihe.values().stream())
                .anyMatch(Platz::istVerkauft);
    }

    /**
     * Liefert die {@link VorstellungId} der Vorstellung mit dem gegebenen Film, Saal und Beginn.
     * Ersetzt die früher fest verdrahteten UUIDs aus der {@code data.sql}.
     */
    protected VorstellungId vorstellungId(String film, String saal, LocalDateTime beginn) {
        return aktuelleVorstellungen.finde(new Film(film), new Saal(saal), new Beginn(beginn))
                .map(Vorstellung::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Keine Vorstellung gefunden für " + film + " / " + saal + " / " + beginn));
    }
}
