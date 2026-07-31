package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers.VorstellungEntityMapper;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AktuelleVorstellungenImpl implements AktuelleVorstellungen {

    private final VorstellungRepository vorstellungRepository;

    private final VorstellungEntityMapper vorstellungMapper;

    @Override
    public List<Vorstellung> alleVorstellungen() {
        var vorstellungen = vorstellungRepository.findAll();
        return vorstellungen.stream().map(vorstellungMapper::toDomain).toList();
    }

    @Override
    public Vorstellung holeVorstellung(VorstellungId vorstellungId) {
        var vorstellung = vorstellungRepository.findById(vorstellungId.uuid());
        RessourceNichtGefunden.wenn(vorstellung.isEmpty(), "Vorstellung zu " + vorstellungId + " existiert nicht");
        return vorstellungMapper.toDomain(vorstellung.get());
    }

    @Override
    public Optional<Vorstellung> finde(Film film, Saal saal, Beginn beginn) {
        return vorstellungRepository.findByFilmAndSaalAndBeginn(film.name(), saal.name(), beginn.zeitpunkt())
                .map(vorstellungMapper::toDomain);
    }

    @Override
    public void speichere(Vorstellung vorstellung) {
        vorstellungRepository.save(vorstellungMapper.toEntity(vorstellung));
    }
}
