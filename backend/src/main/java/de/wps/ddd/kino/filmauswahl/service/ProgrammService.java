package de.wps.ddd.kino.filmauswahl.service;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmauswahl.data.AktuelleFilme;
import de.wps.ddd.kino.filmauswahl.data.Film;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationService
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgrammService {

    private final AktuelleFilme aktuelleFilme;

    public List<Film> holeVorstellungenFuerTag(LocalDate datum) {
        LocalDateTime start = datum.atStartOfDay();
        LocalDateTime ende = LocalTime.MAX.atDate(datum);
        return aktuelleFilme.findeVorstellungenZwischen(start, ende);
    }
}
