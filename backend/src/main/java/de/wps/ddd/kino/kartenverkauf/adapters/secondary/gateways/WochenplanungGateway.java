package de.wps.ddd.kino.kartenverkauf.adapters.secondary.gateways;

import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungKategorie;
import de.wps.ddd.kino.wochenplanung.WochenplanDto;
import de.wps.ddd.kino.wochenplanung.Wochenplanauskunft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sekundäradapter (Anti-Corruption-Layer): fragt die exponierte {@link Wochenplanauskunft} der
 * Wochenplanung ab und übersetzt die Vorstellungen des {@link WochenplanDto} in die
 * Kartenverkauf-Domäne. Die Bestuhlung ist nicht Teil des DTOs — sie stammt aus den
 * kartenverkauf-eigenen Stammdaten.
 */
@Component
@RequiredArgsConstructor
public class WochenplanungGateway implements de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Wochenplaene {

    private final Wochenplanauskunft wochenplanauskunft;

    @Override
    public List<Vorstellung> holeWochenplan(int jahr, int kalenderwoche) {
        return wochenplanauskunft.holeWochenplan(jahr, kalenderwoche).vorstellungen().stream()
                .map(this::toVorstellung)
                .toList();
    }

    private Vorstellung toVorstellung(WochenplanDto.VorstellungDto dto) {
        return new Vorstellung(
                new VorstellungId(dto.id()),
                new Saal(dto.saalName()),
                new Beginn(dto.beginn()),
                new Film(dto.filmTitel()),
                VorstellungKategorie.valueOf(dto.kategorie()));
    }
}
