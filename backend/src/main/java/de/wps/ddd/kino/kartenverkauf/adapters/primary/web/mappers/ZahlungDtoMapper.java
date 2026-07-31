package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.GeldbetragDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.VerkaufsvorgangDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.ZahlungsvorgangDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.ZahlungsstatusDto;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZahlungDtoMapper {

    private final SaalplanDtoMapper saalplanMapper;

    public VerkaufsvorgangDto toDto(Verkaufsvorgang verkaufsvorgang) {
        return new VerkaufsvorgangDto(
                verkaufsvorgang.getAuftragsnummer().nummer().toString(),
                verkaufsvorgang.getVorstellungId().uuid().toString(),
                saalplanMapper.toDto(verkaufsvorgang.getGewaehltePlaetze()),
                toDto(verkaufsvorgang.getGesamtpreis()));
    }

    public ZahlungsvorgangDto toDto(Zahlungsvorgang zahlungsvorgang) {
        return new ZahlungsvorgangDto(
                zahlungsvorgang.getId().wert().toString(),
                zahlungsvorgang.getAnlauf(),
                toDto(zahlungsvorgang.getBetrag()),
                zahlungsvorgang.getStatus().toString());
    }

    public GeldbetragDto toDto(Geldbetrag geldbetrag) {
        return new GeldbetragDto(geldbetrag.getBetrag(), geldbetrag.getWaehrung().toString());
    }

    public ZahlungsstatusDto toDto(Zahlungsstatus status) {
        return new ZahlungsstatusDto(status.toString());
    }
}
