package de.wps.ddd.kino.filmvorfuehrung.application;

import org.springframework.stereotype.Service;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmvorfuehrung.domain.FilmvorfuehrungRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.Film;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmId;
import de.wps.ddd.kino.filmvorfuehrung.domain.film.FilmRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Filmvorfuehrung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;
import de.wps.ddd.kino.filmvorfuehrung.dtos.FilmvorfuehrungResponseDto;

@ApplicationService 
@Service 
public class FilmvorfuehrungService {

    private final FilmvorfuehrungRepository filmvorfuehrungRepository;
    private final FilmRepository filmRepository;

    public FilmvorfuehrungService(FilmvorfuehrungRepository filmvorfuehrungRepository, FilmRepository filmRepository) {
        this.filmvorfuehrungRepository = filmvorfuehrungRepository;
        this.filmRepository = filmRepository;
    }

    public FilmvorfuehrungResponseDto holeFilmvorfuehrungMitVorfuehrungId(VorfuehrungId vorfuehrungId) {
        // Holt die Filmvorführung aus dem Filmvorführungsrepo
        Filmvorfuehrung filmvorfuehrung = filmvorfuehrungRepository.hole(vorfuehrungId)
            .orElseThrow(() -> new RuntimeException("Vorführung mit der ID " + vorfuehrungId + " nicht gefunden"));
        
        // Holt den Film aus dem Filmrepo
        FilmId filmId = filmvorfuehrung.getFilmId();
        Film film = filmRepository.hole(filmId)
            .orElseThrow(() -> new RuntimeException("Film mit der ID " + filmId + " nicht gefunden"));
        
        // Gibt das Dto zurück
        FilmvorfuehrungResponseDto dto = new FilmvorfuehrungResponseDto(
            vorfuehrungId,
            filmvorfuehrung.getGeplanterBeginn(),
            filmvorfuehrung.getTatsaechlicherBeginn(),
            filmvorfuehrung.getSaal(),
            filmvorfuehrung.getStatus(),
            film.getFilmtitel(),
            film.getFilmlaufzeit(),
            filmvorfuehrung.getUnterbrechungen()
        );
        return dto;
    }

    public Film holeFilmMitVorfuehrungId(VorfuehrungId vorfuehrungId) {
        Filmvorfuehrung filmvorfuehrung = filmvorfuehrungRepository.hole(vorfuehrungId)
            .orElseThrow(() -> new RuntimeException("Vorführung mit der ID " + vorfuehrungId + " nicht gefunden"));
        
        FilmId filmId = filmvorfuehrung.getFilmId();
        Film film = filmRepository.hole(filmId)
            .orElseThrow(() -> new RuntimeException("Film mit der ID " + filmId + " nicht gefunden"));
        return film;
    }
}