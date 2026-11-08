package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.KinokarteDto;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KartenDtoMapper {

    public List<KinokarteDto> toDto(List<Kinokarte> kinokarten) {
        return kinokarten.stream().map(this::toDto).toList();
    }

    public KinokarteDto toDto(Kinokarte kinokarte) {
        return new KinokarteDto(
                kinokarte.getFilm().name(),
                kinokarte.getBeginn().zeitpunkt().toString(),
                kinokarte.getSaal().name(),
                kinokarte.getReihe().nummer(),
                kinokarte.getPlatz().nummer(),
                kinokarte.getId().wert()
        );
    }
}
