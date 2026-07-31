package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers.VerkaufsvorgangEntityMapper;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsvorgaenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerkaufsvorgaengeImpl implements Verkaufsvorgaenge {

    private final VerkaufsvorgangRepository verkaufsvorgangRepository;

    private final VerkaufsvorgangEntityMapper verkaufsvorgangMapper;

    @Override
    public void speichere(Verkaufsvorgang verkaufsvorgang) {
        var entity = verkaufsvorgangMapper.toEntity(verkaufsvorgang);
        verkaufsvorgangRepository.save(entity);
    }

    @Override
    public Verkaufsvorgang hole(Auftragsnummer auftragsnummer) {
        var entity = verkaufsvorgangRepository.findById(auftragsnummer.nummer());
        RessourceNichtGefunden.wenn(entity.isEmpty(), "Verkaufsvorgang " + auftragsnummer + " existiert nicht");
        return verkaufsvorgangMapper.toDomain(entity.get());
    }

    @Override
    public Verkaufsvorgang holeZuZahlungsvorgang(ZahlungsvorgangId zahlungsvorgangId) {
        var entity = verkaufsvorgangRepository.findByZahlungsvorgang(zahlungsvorgangId.wert());
        RessourceNichtGefunden.wenn(entity.isEmpty(), "Zu Zahlungsvorgang " + zahlungsvorgangId + " existiert kein Verkaufsvorgang");
        return verkaufsvorgangMapper.toDomain(entity.get());
    }
}
