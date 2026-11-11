package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence;

import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers.KinokarteEntityMapper;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.repositories.KinokarteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KinokartenRepositoryImpl implements KinokartenRepository {

    private final KinokarteJpaRepository repository;
    private final KinokarteEntityMapper mapper;

    @Override
    public Optional<Kinokarte> hole(KinokartenId id) {
        return repository.findById(id.id())
                .map(mapper::toDomain);
    }

    @Override
    public void speichere(Kinokarte kinokarte) {
        repository.save(mapper.toEntity(kinokarte));
    }
}
