package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.einlasskontrolle.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EinlasskontrolleServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");
    private static final LocalDate TAG = LocalDate.of(2026, 11, 10);

    private static final KinokartenId KARTEN_ID = new KinokartenId(UUID.randomUUID());

    private static final VorstellungsId VORSTELLUNGS_ID = new VorstellungsId(UUID.randomUUID());

    @Mock
    private KinokartenRepository kinokartenRepository;

    private FilmvorstellungenRepository filmvorstellungenRepository;

    private Filmvorstellung vorstellung;
    private Kinokarte karte;

    @BeforeEach
    void setUp() {
        filmvorstellungenRepository = mock(FilmvorstellungenRepository.class, CALLS_REAL_METHODS);

        vorstellung = new Filmvorstellung(
                VORSTELLUNGS_ID,
                new GeplanteStartzeit(TAG.atTime(20, 0)),
                new GeplanteEndzeit(TAG.atTime(22, 0)));

        karte = new Kinokarte(
                KARTEN_ID,
                new Reihe(5),
                new Sitzplatz(12),
                Saal.SAAL_1,
                VORSTELLUNGS_ID);

        doReturn(Optional.of(karte))
                .when(kinokartenRepository).hole(KARTEN_ID);

        doReturn(Optional.of(vorstellung))
                .when(filmvorstellungenRepository).hole(VORSTELLUNGS_ID);
    }

    private EinlasskontrolleService service(LocalTime uhrzeit) {
        Clock clock = Clock.fixed(
                TAG.atTime(uhrzeit).atZone(ZONE).toInstant(), ZONE);

        return new EinlasskontrolleService(
                kinokartenRepository,
                filmvorstellungenRepository,
                clock);
    }

    // 1. HAPPY PATH -> Gültige Karte: Einlass erlaubt und Karte gespeichert
    @Test
    void erlaubtEinlassUndEntwertetKarte() {
        Einlassentscheidung entscheidung = service(LocalTime.of(19, 45))
                .kinokarteValidierenUndEntwerten(KARTEN_ID, VORSTELLUNGS_ID);

        assertThat(entscheidung)
                .isInstanceOf(Einlassentscheidung.Erlaubt.class);

        assertThat(karte.getEntwertungszeitpunkt())
                .isNotNull();

        verify(kinokartenRepository).speichere(karte);
    }

    // 2. Bereits entwertete Karte: Einlass abgelehnt
    @Test
    void lehntBereitsEntwerteteKarteAb() {
        Entwertungszeitpunkt vorher = new Entwertungszeitpunkt(TAG.atTime(19, 40));

        karte = Kinokarte.rekonstruiere(
                KARTEN_ID,
                new Reihe(5),
                new Sitzplatz(12),
                Saal.SAAL_1,
                VORSTELLUNGS_ID,
                vorher);

        doReturn(Optional.of(karte))
                .when(kinokartenRepository).hole(KARTEN_ID);

        Einlassentscheidung entscheidung = service(LocalTime.of(19, 45))
                .kinokarteValidierenUndEntwerten(KARTEN_ID, VORSTELLUNGS_ID);

        assertThat(entscheidung).isEqualTo(
                new Einlassentscheidung.Abgelehnt(
                        Ablehnungsgrund.BEREITS_ENTWERTET));

        verify(kinokartenRepository, never()).speichere(any());
    }

    // 3. Außerhalb des Einlassfensters: Einlass abgelehnt
    @Test
    void lehntEinlassZuFruehAb() {
        Einlassentscheidung entscheidung = service(LocalTime.of(19, 29, 59))
                .kinokarteValidierenUndEntwerten(KARTEN_ID, VORSTELLUNGS_ID);

        assertThat(entscheidung).isEqualTo(
                new Einlassentscheidung.Abgelehnt(
                        Ablehnungsgrund.ZU_FRUEH));

        assertThat(karte.getEntwertungszeitpunkt()).isNull();

        verify(kinokartenRepository, never()).speichere(any());
    }
}
