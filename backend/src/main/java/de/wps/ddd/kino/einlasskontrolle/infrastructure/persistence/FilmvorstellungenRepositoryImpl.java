package de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence;

import de.wps.ddd.kino.einlasskontrolle.domain.Filmvorstellung;
import de.wps.ddd.kino.einlasskontrolle.domain.FilmvorstellungenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.VorstellungsId;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.mappers.FilmvorstellungEntityMapper;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.persistence.repositories.FilmvorstellungJpaRepository;
import lombok.RequiredArgsConstructor;
import org.jmolecules.ddd.annotation.Repository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Repository
@Component 
@RequiredArgsConstructor
public class FilmvorstellungenRepositoryImpl implements FilmvorstellungenRepository
{
    private final FilmvorstellungJpaRepository repository;
    private final FilmvorstellungEntityMapper mapper;

    // updatable=false in der Entity verhindert, dass die VorstellungId geändert wird,
    // daher ist es sicher, dass die Id beim Speichern nicht überschrieben wird.
    // Im Domain-Objekt gibt es außerdem keine Setter.
    @Override
    public void fuegeVorstellungHinzu(Filmvorstellung filmvorstellung) {
        repository.save(mapper.toEntity(filmvorstellung));
    }

    @Override
    public Optional<Filmvorstellung> hole(VorstellungsId id) {
        return repository.findById(id.id())
                .map(mapper::toDomain);
    }
}
