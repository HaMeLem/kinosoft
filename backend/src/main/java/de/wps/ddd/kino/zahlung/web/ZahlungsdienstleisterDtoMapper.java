package de.wps.ddd.kino.zahlung.web;

import de.wps.ddd.kino.zahlung.domain.Zahlung;
import org.springframework.stereotype.Component;

@Component
public class ZahlungsdienstleisterDtoMapper {

    public ZahlungDto toDto(Zahlung zahlung) {
        return new ZahlungDto(
                zahlung.getReferenz().wert().toString(),
                zahlung.getBetrag().cent(),
                zahlung.getStatus().name());
    }
}
