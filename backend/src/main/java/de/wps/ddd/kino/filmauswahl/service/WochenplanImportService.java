package de.wps.ddd.kino.filmauswahl.service;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmauswahl.data.AktuelleFilme;
import de.wps.ddd.kino.filmauswahl.data.Film;
import de.wps.ddd.kino.filmauswahl.data.Saal;
import de.wps.ddd.kino.filmauswahl.data.Saele;
import de.wps.ddd.kino.filmauswahl.data.Vorstellung;
import de.wps.ddd.kino.filmauswahl.data.Vorstellungen;
import de.wps.ddd.kino.wochenplanung.WochenplanDto;
import de.wps.ddd.kino.wochenplanung.Wochenplanauskunft;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Übernimmt nach dem {@code WochenplanErstelltDto} den Wochenplan der Wochenplanung in den eigenen
 * Speicher der Filmauswahl (Anti-Corruption-Layer). Fragt dazu die exponierte
 * {@link Wochenplanauskunft} ab und bildet das neutrale {@link WochenplanDto} auf Filme, Säle und
 * Vorstellungen der Filmauswahl ab; die Vorstellungen werden dabei je Film gruppiert.
 */
@ApplicationService
@Service
@RequiredArgsConstructor
@Slf4j
public class WochenplanImportService {

    private final Wochenplanauskunft wochenplanauskunft;
    private final AktuelleFilme aktuelleFilme;
    private final Saele saele;
    private final Vorstellungen vorstellungen;

    @Transactional
    public void importiere(int jahr, int kalenderwoche) {
        var wochenplanDto = wochenplanauskunft.holeWochenplan(jahr, kalenderwoche);

        log.info("Filmauswahl importiert Wochenplan KW {}/{}: {} Vorstellungen", kalenderwoche, jahr, wochenplanDto.vorstellungen().size());

        var vorstellungenNachFilm = wochenplanDto.vorstellungen().stream()
                .collect(Collectors.groupingBy(WochenplanDto.VorstellungDto::filmTitel, LinkedHashMap::new, Collectors.toList()));

        for (WochenplanDto.FilmDto filmDto : wochenplanDto.filme()) {
            var film = uebernehmeFilm(filmDto);
            for (WochenplanDto.VorstellungDto vorstellungDto : vorstellungenNachFilm.getOrDefault(filmDto.titel(), List.of())) {
                uebernehmeVorstellung(film, vorstellungDto);
            }
        }
    }

    private Film uebernehmeFilm(WochenplanDto.FilmDto filmDto) {
        var film = aktuelleFilme.findByTitel(filmDto.titel()).orElseGet(Film::new);
        film.setTitel(filmDto.titel());
        film.setLaufzeit(filmDto.laufzeit());
        film.setPosterUrl(filmDto.posterUrl());
        film.setFsk(filmDto.fsk());
        film.setBeschreibung(filmDto.beschreibung());
        film.setGenre(filmDto.genre());
        film.setHauptdarsteller(filmDto.hauptdarsteller());
        film.setRegie(filmDto.regie());
        film.setSprache(filmDto.sprache());
        return aktuelleFilme.save(film);
    }

    private void uebernehmeVorstellung(Film film, WochenplanDto.VorstellungDto vorstellungDto) {
        var saal = holeOderErzeugeSaal(vorstellungDto.saalName());
        var vorstellung = vorstellungen.findByUuid(vorstellungDto.id()).orElseGet(Vorstellung::new);
        vorstellung.setUuid(vorstellungDto.id());
        vorstellung.setFilmId(film.getId());
        vorstellung.setBeginn(vorstellungDto.beginn());
        vorstellung.setSaal(saal);
        vorstellungen.save(vorstellung);
    }

    private Saal holeOderErzeugeSaal(String name) {
        return saele.findByName(name).orElseGet(() -> saele.save(new Saal(null, name)));
    }
}
