package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers.KinokarteEntityMapper;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Kinokarten;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KinokartenImpl implements Kinokarten {

    private final KinokarteRepository kinokarteRepository;

    private final KinokarteEntityMapper kinokarteMapper;

    @Override
    public void speichere(List<Kinokarte> kinokarten) {
        var entities = kinokarten.stream().map(kinokarteMapper::toEntity).toList();
        kinokarteRepository.saveAll(entities);
    }

    @Override
    public List<Kinokarte> finde(Auftragsnummer auftragsnummer) {
        return kinokarteRepository.findByAuftragsnummer(auftragsnummer.nummer()).stream()
                .map(kinokarteMapper::toDomain)
                .toList();
    }
}
