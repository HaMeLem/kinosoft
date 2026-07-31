package de.wps.ddd.kino.wochenplanung.application.fixtures;

import de.wps.ddd.kino.common.fixtures.Fixture;
import de.wps.ddd.kino.wochenplanung.WochenplanErstelltDto;
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
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplaene;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Baut beim Start den Wochenplan der aktuellen ISO-Kalenderwoche (Mo–So) im Code auf und veröffentlicht
 * anschließend das {@link WochenplanErstelltDto}. Der Bezugstag stammt aus dem injizierten {@link Clock};
 * in Produktion ist das die Systemuhr, sodass die Demo stets „diese Woche" zeigt. Tests pinnen die Uhr
 * fest auf 2025-03-19 (KW 12/2025) und reproduzieren so exakt denselben Fahrplan.
 * <p>
 * Ersetzt die frühere {@code data.sql}-Bestückung: Filmauswahl und Kartenverkauf beziehen ihre Sichten
 * nach dem Ereignis selbst über die {@code Wochenplanauskunft}.
 * <p>
 * Die Veröffentlichung muss in einer Transaktion erfolgen, sonst feuert der nachgelagerte
 * {@code @ApplicationModuleListener} (async nach Commit) nicht.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WochenplanFixture implements Fixture {

    private static final String STAR_BOARS = "Star Boars";
    private static final String GUARDIANS = "Guardians of the Lunacy";
    private static final String BACK_TO_THE_FUTURA = "Back to the Futura";
    private static final String CLOWN_WARS = "Clown Wars";
    private static final String FAST_AND_CURIOUS = "The Fast and the Curious";

    private static final String GROSSER_SAAL = "großer Saal";
    private static final String KLEINER_SAAL = "kleiner Saal";

    private final Filmkatalog filmkatalog;
    private final Wochenplaene wochenplaene;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    @Transactional
    @Override
    public void install() {
        var heute = LocalDate.now(clock);
        var montag = heute.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int jahr = heute.get(IsoFields.WEEK_BASED_YEAR);
        int kalenderwoche = heute.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        log.info("Erzeuge Wochenplan KW {}/{}...", kalenderwoche, jahr);

        var filme = erzeugeFilme(heute);
        var wochenplan = erzeugeWochenplan(jahr, kalenderwoche, montag, filme);

        events.publishEvent(new WochenplanErstelltDto(jahr, kalenderwoche));
        log.info("Wochenplan veröffentlicht: {} Vorstellungen", wochenplan.getVorstellungen().size());
    }

    private Map<String, Film> erzeugeFilme(LocalDate heute) {
        // Lizenzfrist großzügig um die aktuelle Woche: vom Monatsersten bis Jahresende.
        var lizenzBeginn = heute.withDayOfMonth(1);
        var lizenzEnde = heute.with(TemporalAdjusters.lastDayOfYear());
        var filme = new LinkedHashMap<String, Film>();
        registriereFilm(filme, new Film(new Filmtitel(STAR_BOARS), 125, "assets/Star_Boars.jpeg",
                """
                        In einer weit, weit entfernten Galaxie kämpfen mutige Wildschweine gegen das tyrannische Imperium.
                        Angeführt von Luke Stywalker, müssen sie sich mit Lichttrüffeln und telepathischen Grunzkraftfähigkeiten gegen den dunklen Lord Swineous behaupten.
                        Ein episches Sci-Fi-Abenteuer voller Action, Humor und intergalaktischem Speckduft!""",
                "Sci-Fi, Comedy", "Luke Stywalker", "George Laxus", "deutsch", Altersfreigabe.FSK12,
                new Verleih("Galaktik Filmverleih", Geldbetrag.euro(1500, 0), lizenzBeginn, lizenzEnde)));

        registriereFilm(filme, new Film(new Filmtitel(GUARDIANS), 95, "assets/Guardians_Of_The_Lunacy.jpeg",
                """
                        Eine Truppe aus abgedrehten Außenseitern wird widerwillig zum Schutz des Universums verpflichtet – und das ist kein gutes Zeichen.
                        Chris Plattfall und sein chaotisches Team aus galaktischen Verrückten stürzen sich in explosive Abenteuer voller skurriler Aliens,
                        dummer Sprüche und unerwarteter Heldentaten. Werden sie das Universum retten? Wahrscheinlich nicht. Aber es wird lustig!""",
                "Sci-Fi, Comedy", "Chris Plattfall", "James Gunner", "englisch", Altersfreigabe.FSK6,
                new Verleih("Mondschein Pictures", Geldbetrag.euro(1200, 0), lizenzBeginn, lizenzEnde)));

        registriereFilm(filme, new Film(new Filmtitel(BACK_TO_THE_FUTURA), 116, "assets/Back_To_The_Futura.jpeg",
                """
                        Als die junge Marty McGigawatts mit einer experimentellen Zeitmaschine in die Zukunft reist, findet sie sich in einer dystopischen Megacity wieder,
                        in der Roboter die Welt regieren. Mit der Hilfe eines exzentrischen Erfinders und einem Hoverboard muss sie den Lauf der Geschichte ändern,
                        bevor sie in einer Endlosschleife der Zeit gefangen bleibt.""",
                "Sci-Fi, Adventure", "Marty McGigawatts", "Robert Zoomekis", "deutsch", Altersfreigabe.FSK12,
                new Verleih("Zeitreise Verleih", Geldbetrag.euro(1400, 0), lizenzBeginn, lizenzEnde)));

        registriereFilm(filme, new Film(new Filmtitel(CLOWN_WARS), 105, "assets/Clown_Wars.jpeg",
                """
                        Die Erde wird von einer Horde außerirdischer Clowns angegriffen, die nichts anderes wollen, als die Menschheit mit tödlichen Gags zu unterwerfen.
                        Nur eine Gruppe rebellischer Spaßmacher kann sich der Bedrohung entgegenstellen. Ein intergalaktisches Spektakel voller Ballontier-Kriege,
                        Killer-Jojos und einem epischen Showdown in der Zirkusarena des Todes.""",
                "Sci-Fi, Horror, Comedy", "Penny Wisecrack", "Tim Burtonisch", "englisch", Altersfreigabe.FSK18,
                new Verleih("Zirkus des Grauens Film", Geldbetrag.euro(1300, 0), lizenzBeginn, lizenzEnde)));

        registriereFilm(filme, new Film(new Filmtitel(FAST_AND_CURIOUS), 135, "assets/The_Fast_And_The_Curious.jpeg",
                """
                        The Fast and the Curious ist ein actiongeladener Film über eine Bande von hochintelligenten Straßenkatzen, die illegale Straßenrennen fahren und geheime Raubüberfälle planen.
                        Angeführt von der waghalsigen und charismatischen Kätzin Velo, entdeckt das Team, dass eine rivalisierende Hunde-Gang versucht, die Straßen zu übernehmen.
                        Während atemberaubender Verfolgungsjagden, waghalsiger Stunts und cleverer Pläne müssen die Katzen nicht nur ihre Revierhoheit verteidigen, sondern auch ein letztes, spektakuläres Rennen gewinnen, um ihre Freiheit zu sichern.
                        Ein rasanter Mix aus Action, Humor und katzenhafter Cleverness!""",
                "Action, Adventure, Tierfilm", "Cat Moss", "Rob Kitten", "deutsch", Altersfreigabe.FSK16,
                new Verleih("Schnurrende Motoren Verleih", Geldbetrag.euro(1600, 0), lizenzBeginn, lizenzEnde)));
        return filme;
    }

    private @NonNull Wochenplan erzeugeWochenplan(int jahr, int kalenderwoche, LocalDate montag, Map<String, Film> filme) {
        var wochenplan = Wochenplan.fuer(new Kalenderwoche(jahr, kalenderwoche));
        planeVorstellungen(wochenplan, montag, filme);
        wochenplaene.speichere(wochenplan);
        return wochenplan;
    }

    // Der Fahrplan ist ein Muster aus Wochentag + Uhrzeit; die konkreten Termine ergeben sich aus dem
    // Montag der Bezugswoche (siehe plane(...)). So bleibt das Programm relativ zum aktuellen Datum.
    private void planeVorstellungen(Wochenplan wochenplan, LocalDate montag, Map<String, Film> filme) {
        plane(wochenplan, montag, filme.get(GUARDIANS), KLEINER_SAAL, Vorstellungskategorie.Kinderfilm, DayOfWeek.MONDAY, LocalTime.of(15, 0));
        plane(wochenplan, montag, filme.get(FAST_AND_CURIOUS), GROSSER_SAAL, Vorstellungskategorie.PrimeTime, DayOfWeek.MONDAY, LocalTime.of(20, 0));
        plane(wochenplan, montag, filme.get(STAR_BOARS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.TUESDAY, LocalTime.of(15, 30));
        plane(wochenplan, montag, filme.get(BACK_TO_THE_FUTURA), GROSSER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.TUESDAY, LocalTime.of(15, 0));
        plane(wochenplan, montag, filme.get(CLOWN_WARS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.TUESDAY, LocalTime.of(22, 30));
        plane(wochenplan, montag, filme.get(GUARDIANS), GROSSER_SAAL, Vorstellungskategorie.Kinderfilm, DayOfWeek.WEDNESDAY, LocalTime.of(14, 30));
        plane(wochenplan, montag, filme.get(FAST_AND_CURIOUS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.WEDNESDAY, LocalTime.of(15, 0));
        plane(wochenplan, montag, filme.get(FAST_AND_CURIOUS), GROSSER_SAAL, Vorstellungskategorie.PrimeTime, DayOfWeek.WEDNESDAY, LocalTime.of(20, 30));
        plane(wochenplan, montag, filme.get(CLOWN_WARS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.WEDNESDAY, LocalTime.of(22, 30));
        plane(wochenplan, montag, filme.get(GUARDIANS), KLEINER_SAAL, Vorstellungskategorie.Kinderfilm, DayOfWeek.THURSDAY, LocalTime.of(14, 30));
        plane(wochenplan, montag, filme.get(BACK_TO_THE_FUTURA), GROSSER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.THURSDAY, LocalTime.of(15, 30));
        plane(wochenplan, montag, filme.get(GUARDIANS), GROSSER_SAAL, Vorstellungskategorie.PrimeTime, DayOfWeek.THURSDAY, LocalTime.of(20, 0));
        plane(wochenplan, montag, filme.get(FAST_AND_CURIOUS), GROSSER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.FRIDAY, LocalTime.of(15, 0));
        plane(wochenplan, montag, filme.get(BACK_TO_THE_FUTURA), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.FRIDAY, LocalTime.of(16, 15));
        plane(wochenplan, montag, filme.get(BACK_TO_THE_FUTURA), GROSSER_SAAL, Vorstellungskategorie.PrimeTime, DayOfWeek.FRIDAY, LocalTime.of(20, 30));
        plane(wochenplan, montag, filme.get(CLOWN_WARS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.FRIDAY, LocalTime.of(22, 30));
        plane(wochenplan, montag, filme.get(BACK_TO_THE_FUTURA), GROSSER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.SATURDAY, LocalTime.of(14, 30));
        plane(wochenplan, montag, filme.get(STAR_BOARS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.SATURDAY, LocalTime.of(15, 30));
        plane(wochenplan, montag, filme.get(GUARDIANS), KLEINER_SAAL, Vorstellungskategorie.Kinderfilm, DayOfWeek.SATURDAY, LocalTime.of(19, 30));
        plane(wochenplan, montag, filme.get(STAR_BOARS), GROSSER_SAAL, Vorstellungskategorie.PrimeTime, DayOfWeek.SATURDAY, LocalTime.of(20, 45));
        plane(wochenplan, montag, filme.get(FAST_AND_CURIOUS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.SUNDAY, LocalTime.of(14, 30));
        plane(wochenplan, montag, filme.get(GUARDIANS), GROSSER_SAAL, Vorstellungskategorie.Kinderfilm, DayOfWeek.SUNDAY, LocalTime.of(15, 45));
        plane(wochenplan, montag, filme.get(FAST_AND_CURIOUS), GROSSER_SAAL, Vorstellungskategorie.PrimeTime, DayOfWeek.SUNDAY, LocalTime.of(19, 30));
        plane(wochenplan, montag, filme.get(CLOWN_WARS), KLEINER_SAAL, Vorstellungskategorie.Standard, DayOfWeek.SUNDAY, LocalTime.of(22, 30));
    }

    private void plane(Wochenplan wochenplan, LocalDate montag, Film film, String saalname, Vorstellungskategorie kategorie, DayOfWeek tag, LocalTime uhrzeit) {
        var beginn = LocalDateTime.of(montag.plusDays(tag.getValue() - 1), uhrzeit);
        wochenplan.planeVorstellung(Vorstellung.plane(film, new Saalname(saalname), kategorie, beginn));
    }

    private void registriereFilm(Map<String, Film> filme, Film film) {
        filme.put(film.titel().wert(), film);
        filmkatalog.speichere(film);
    }
}
