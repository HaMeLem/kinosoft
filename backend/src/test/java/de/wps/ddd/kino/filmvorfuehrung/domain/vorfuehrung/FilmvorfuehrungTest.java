package de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;


public class FilmvorfuehrungTest {

    private final static VorfuehrungId vorfuehrungId1 = new VorfuehrungId(UUID.randomUUID());
    private final static GeplanterBeginn geplanterBeginn1 = new GeplanterBeginn(LocalDateTime.of(2026,11,22,17,00));
    private final static GeplantesEnde geplantesEnde1 = new GeplantesEnde(LocalDateTime.of(2026,11,22,19,30));
    private final static TatsaechlicherBeginn tatsaechlicherBeginn1 = new TatsaechlicherBeginn(LocalDateTime.of(2026,11,22,17,15));
    private final static FilmId filmId1 = new FilmId(UUID.randomUUID());

    //private LocalDateTime jetzt1 = LocalDateTime.of(2026,11,22,16,30);
    //private LocalDateTime jetzt2 = LocalDateTime.of(2026,11,22,17,01);
    //private LocalDateTime jetzt3 = LocalDateTime.of(2026,11,22,17,17);
    //private LocalDateTime jetzt4 = LocalDateTime.of(2026,11,22,17,45);
    //private LocalDateTime jetzt5 = LocalDateTime.of(2026,11,22,20,00);

    // Unterbrechungen während der Werbung
    private final static UnterbrechungId uuidUnterbrechung1 = new UnterbrechungId(UUID.randomUUID());
    private final static Unterbrechungsbeginn unterbrechungsbeginn1 = new Unterbrechungsbeginn(LocalDateTime.of(2026,11,22,17,20));
    private final static Unterbrechungsende unterbrechungsende1 = new Unterbrechungsende(LocalDateTime.of(2026,11,22,17,22));
    private final static Unterbrechung unterbrechung1 = new Unterbrechung(uuidUnterbrechung1, unterbrechungsbeginn1, unterbrechungsende1);
    private final static UnterbrechungId uuidUnterbrechung2 = new UnterbrechungId(UUID.randomUUID());
    private final static Unterbrechungsbeginn unterbrechungsbeginn2 = new Unterbrechungsbeginn(LocalDateTime.of(2026,11,22,17,23));
    private final static Unterbrechungsende unterbrechungsende2 = new Unterbrechungsende(LocalDateTime.of(2026,11,22,17,24));
    private final static Unterbrechung unterbrechung2 = new Unterbrechung(uuidUnterbrechung2, unterbrechungsbeginn2, unterbrechungsende2);

    // Unterbrechung während des Films
    private final static UnterbrechungId uuidUnterbrechung3 = new UnterbrechungId(UUID.randomUUID());
    private final static Unterbrechungsbeginn unterbrechungsbeginn3 = new Unterbrechungsbeginn(LocalDateTime.of(2026,11,22,18,00));
    private final static Unterbrechungsende unterbrechungsende3 = new Unterbrechungsende(LocalDateTime.of(2026,11,22,18,10));
    private final static Unterbrechung unterbrechung3 = new Unterbrechung(uuidUnterbrechung3, unterbrechungsbeginn3, unterbrechungsende3);

    @BeforeAll
    public static void beforeAll(){
        List<Unterbrechung> unterbrechungen = new ArrayList<>();
        unterbrechungen.add(unterbrechung1);
        unterbrechungen.add(unterbrechung2);
        unterbrechungen.add(unterbrechung3);

        Filmvorfuehrung filmvorfuehrung1 = new Filmvorfuehrung(
            vorfuehrungId1,
            geplanterBeginn1,
            geplantesEnde1,
            tatsaechlicherBeginn1,
            Saal.KLEINER_SAAL, 
            Status.LAUFEND,
            filmId1,
            unterbrechungen);
    }
   
    @Test
    void bestimmeGesamteUnterbrechungszeit_waehrendWerbung(){
        //given
        List<Unterbrechung> unterbrechungen = new ArrayList<>();
        unterbrechungen.add(unterbrechung1);
        unterbrechungen.add(unterbrechung2);
        unterbrechungen.add(unterbrechung3);
        Filmvorfuehrung filmvorfuehrung1 = new Filmvorfuehrung(
            vorfuehrungId1,
            geplanterBeginn1,
            geplantesEnde1,
            tatsaechlicherBeginn1,
            Saal.KLEINER_SAAL, 
            Status.LAUFEND,
            filmId1,
            unterbrechungen);

        //when

        //then

    }

    @Test
    void bestimmeVorfuehrungsphase_vorGeplantemBeginn(){
        //given
        final LocalDateTime jetzt = LocalDateTime.of(2026,11,22,16,30);

        //when

        //then
    }

    @Test
    void bestimmeVorfuehrungsphase_nachGeplantemBeginnVorTatsaechlichemBeginn(){
        //given
        final LocalDateTime jetzt = LocalDateTime.of(2026,11,22,17,01);

        //when


        //then
    }

    @Test
    void bestimmeVorfuehrungsphase_waehrendWerbung(){
        //given
        final LocalDateTime jetzt = LocalDateTime.of(2026,11,22,17,17);
   
        //when


        //then
    }

    @Test
    void bestimmeVorfuehrungsphase_waehrendFilm(){
        //given
        final LocalDateTime jetzt = LocalDateTime.of(2026,11,22,17,45);
    

        //when


        //then
    }

    @Test
    void bestimmeVorfuehrungsphase_nachVorfuehrungsende(){
        //given
        final LocalDateTime jetzt = LocalDateTime.of(2026,11,22,20,00);

        //when


        //then
    }
}
