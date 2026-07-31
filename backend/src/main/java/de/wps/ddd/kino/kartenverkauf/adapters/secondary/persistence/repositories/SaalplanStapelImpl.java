package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers.SaalplanEntityMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model.SaalplanEntity;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaalplanStapelImpl implements SaalplanStapel {

    private final SaalplanRepository saalplanRepository;
    private final SaalplanEntityMapper saalplanMapper;

    @Override
    public Saalplan holeSaalplan(VorstellungId vorstellungId) {
        SaalplanEntity saalplanEntity = saalplanRepository.findByVorstellungUUID(vorstellungId.uuid());
        RessourceNichtGefunden.wenn(saalplanEntity == null, "Saalplan zu Vorstellung " + vorstellungId + " existiert nicht");
        return saalplanMapper.toDomain(saalplanEntity);
    }

    @Override
    public boolean existiertSaalplan(VorstellungId vorstellungId) {
        return saalplanRepository.findIdByVorstellungUUID(vorstellungId.uuid()).isPresent();
    }

    @Override
    public void legeZurueck(Saalplan saalplan) {
        Integer saalplanEntityId = saalplanRepository.findIdByVorstellungUUID(saalplan.getVorstellungId().uuid()).orElse(null);
        SaalplanEntity saalplanEntity = saalplanMapper.toEntity(saalplan, saalplanEntityId);
        saalplanRepository.save(saalplanEntity);
    }
}
