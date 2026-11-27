package de.wps.ddd.kino.filmvorfuehrung.domain.film;





import java.util.List;

public interface FilmRepository {

    // Optional <Film> holeFilmüberTitel ( // behelf da FilmId noch nicht existiert
    List <Film> fuegeFilmeHinzu (List <Film> film);
    //perspektivisch hole (über id) ist dann die Standardweise, wie geholt wird
}
