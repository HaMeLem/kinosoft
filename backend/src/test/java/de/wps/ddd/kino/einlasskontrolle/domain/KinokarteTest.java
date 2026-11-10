package de.wps.ddd.kino.einlasskontrolle.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class KinokarteTest
{

    // Die Vorstellung läuft von 20:00 bis 22:00 Uhr, der Einlass ist ab 19:30 Uhr offen
    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 10, 20, 0);
    private static final LocalDateTime ENDE = LocalDateTime.of(2026, 11, 10, 22, 0);
    private static final LocalDateTime EINLASS_BEGINN = START.minusMinutes(30);

    private final VorstellungsId vorstellungsId = new VorstellungsId(UUID.randomUUID());
    // die echte Filmvorstellung, die Domain braucht hier keine Fakes oder Mocks
    private final Filmvorstellung vorstellung = new Filmvorstellung(
            vorstellungsId, new GeplanteStartzeit(START), new GeplanteEndzeit(ENDE));

    @Test
    /* Test prüft, ob eine gültige Karte im Einlassfenster entwertet wird und den Platz nennt // HAPPY PATH
     */
    void entwerten_erlaubtEinlassUndSetztDenEntwertungszeitpunkt() {
        // given
        Kinokarte karte = neueKarteFuer(vorstellungsId);
        LocalDateTime jetzt = START.minusMinutes(10);

        // when
        Einlassentscheidung entscheidung = karte.entwerten(vorstellung, jetzt);

        // then
        // erwarten, dass Saal, Reihe und Sitzplatz der Karte zurückgegeben werden
        assertThat(entscheidung).isEqualTo(
                new Einlassentscheidung.Erlaubt(Saal.SAAL_1, new Reihe(5), new Sitzplatz(12)));
        // die Karte merkt sich, wann sie entwertet wurde
        assertThat(karte.getEntwertungszeitpunkt()).isEqualTo(new Entwertungszeitpunkt(jetzt));
    }

    // ---------- Ticket bereits entwertet ----------

    @Test
    /* Ticket bereits entwertet: zweiter Einlassversuch wird abgelehnt, Entwertungszeitpunkt bleibt der erste
     */
    void entwerten_lehntZweitenEinlassAb() {
        // given
        Kinokarte karte = neueKarteFuer(vorstellungsId);
        karte.entwerten(vorstellung, START.minusMinutes(10));

        // when
        // der zweite Versuch mit derselben Karte
        Einlassentscheidung zweiterVersuch = karte.entwerten(vorstellung, START.minusMinutes(5));

        // then
        assertThat(zweiterVersuch).isEqualTo(new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.BEREITS_ENTWERTET));
    }

    @Test
    /* Test prüft, dass der ERSTE Einlasszeitpunkt erhalten bleibt (wichtig für die Meldung "Erster Einlass am ...")
     */
    void entwerten_behaeltBeiZweitemVersuchDenErstenEntwertungszeitpunkt() {
        // given
        LocalDateTime ersterEinlass = START.minusMinutes(10);
        Kinokarte karte = bereitsEntwerteteKarte(vorstellungsId, ersterEinlass);

        // when
        karte.entwerten(vorstellung, START.minusMinutes(5));

        // then
        assertThat(karte.getEntwertungszeitpunkt()).isEqualTo(new Entwertungszeitpunkt(ersterEinlass));
    }

    // ---------- Falsche Vorstellung ----------

    @Test
    /* Test prüft: Karte gehört zu einer anderen Vorstellung
     */
    void entwerten_lehntKarteFuerAndereVorstellungAbUndEntwertetNicht() {
        // given
        Kinokarte karte = neueKarteFuer(new VorstellungsId(UUID.randomUUID()));

        // when
        Einlassentscheidung entscheidung = karte.entwerten(vorstellung, START.minusMinutes(10));

        // then
        assertThat(entscheidung).isEqualTo(new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.FALSCHE_VORSTELLUNG));
        // eine abgelehnte Karte bleibt unentwertet
        assertThat(karte.getEntwertungszeitpunkt()).isNull();
    }

    @Test
    /* Test prüft: Falsche Vorstellung geht auch vor "bereits entwertet"
     */
    void entwerten_meldetFalscheVorstellungVorBereitsEntwertet() {
        // given
        Kinokarte karte = bereitsEntwerteteKarte(new VorstellungsId(UUID.randomUUID()), START.minusMinutes(40));

        // when
        Einlassentscheidung entscheidung = karte.entwerten(vorstellung, START.minusMinutes(10));

        // then
        assertThat(entscheidung).isEqualTo(new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.FALSCHE_VORSTELLUNG));
    }

    // ---------- Einlassfenster: zu früh ----------

    @Test
    /* Test prüft die Grenze: genau 30 Minuten vor Filmstart ist der Einlass offen
     */
    void entwerten_erlaubtEinlassGenauZuEinlassBeginn() {
        // given
        Kinokarte karte = neueKarteFuer(vorstellungsId);

        // when
        Einlassentscheidung entscheidung = karte.entwerten(vorstellung, EINLASS_BEGINN);

        // then
        assertThat(entscheidung).isInstanceOf(Einlassentscheidung.Erlaubt.class);
    }

    // ---------- Einlassfenster: zu spät ----------

    @Test
    /* Test prüft die Grenze: genau zum geplanten Filmende ist der Einlass noch möglich
     */
    void entwerten_erlaubtEinlassGenauZumGeplantenFilmende() {
        // given
        Kinokarte karte = neueKarteFuer(vorstellungsId);

        // when
        Einlassentscheidung entscheidung = karte.entwerten(vorstellung, ENDE);

        // then
        assertThat(entscheidung).isInstanceOf(Einlassentscheidung.Erlaubt.class);
    }


    // ---------- Reihenfolge der Regeln ----------

    @Test
    /* Test prüft: "bereits entwertet" geht vor dem Einlassfenster
     */
    void entwerten_meldetBereitsEntwertetAuchAusserhalbDesEinlassfensters() {
        // given
        Kinokarte karte = bereitsEntwerteteKarte(vorstellungsId, START.minusMinutes(10));

        // when
        Einlassentscheidung zuFrueh = karte.entwerten(vorstellung, EINLASS_BEGINN.minusHours(1));
        Einlassentscheidung zuSpaet = karte.entwerten(vorstellung, ENDE.plusHours(1));

        // then
        assertThat(zuFrueh).isEqualTo(new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.BEREITS_ENTWERTET));
        assertThat(zuSpaet).isEqualTo(new Einlassentscheidung.Abgelehnt(Ablehnungsgrund.BEREITS_ENTWERTET));
    }

    // ---------- Hilfsmethoden ----------

    private Kinokarte neueKarteFuer(VorstellungsId id) {
        return new Kinokarte(new KinokartenId(UUID.randomUUID()), new Reihe(5), new Sitzplatz(12), Saal.SAAL_1, id);
    }

    // "rekonstruiere" baut eine Karte mit gesetztem Entwertungszeitpunkt, so wie sie aus der Datenbank käme
    private Kinokarte bereitsEntwerteteKarte(VorstellungsId id, LocalDateTime entwertetUm) {
        return Kinokarte.rekonstruiere(
                new KinokartenId(UUID.randomUUID()), new Reihe(5), new Sitzplatz(12), Saal.SAAL_1, id,
                new Entwertungszeitpunkt(entwertetUm));
    }
}