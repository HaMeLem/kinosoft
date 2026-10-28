package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence;

import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers.KinokarteEntityMapper;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KinokartenRepositoryImpl implements KinokartenRepository {

    private final KinokarteJpaRepository repository;
    private final KinokarteEntityMapper mapper;

    @Override
    public Kinokarte hole(KinokartenId id) {
        return repository.findById(id.id())
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Kinokarte nicht gefunden: " + id.id()));
    }

    @Override
    public void speichere(Kinokarte kinokarte) {
        repository.save(mapper.toEntity(kinokarte));
    }
}
