package de.wps.ddd.kino.kartenverkauf.adapters.secondary.eventpublishers;

import de.wps.ddd.kino.kartenverkauf.KinokartenVerkauftDto;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KinokartenVerkauft;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Verkaufsmeldungen;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KinokartenVerkauftPublisher implements Verkaufsmeldungen {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void melde(KinokartenVerkauft kinokartenVerkauft) {
        eventPublisher.publishEvent(toDto(kinokartenVerkauft));
    }

    private KinokartenVerkauftDto toDto(KinokartenVerkauft kinokartenVerkauft) {
        var vorstellung = kinokartenVerkauft.vorstellung();
        var verkaufteKarten = kinokartenVerkauft.kinokarten().stream()
                .map(karte -> new KinokartenVerkauftDto.VerkaufteKarte(
                        karte.getId().wert(), karte.getReihe().nummer(), karte.getPlatz().nummer()))
                .toList();
        return new KinokartenVerkauftDto(
                kinokartenVerkauft.auftragsnummer().nummer(),
                vorstellung.getId().uuid(),
                vorstellung.getFilm().name(),
                vorstellung.getBeginn().zeitpunkt(),
                vorstellung.getSaal().name(),
                verkaufteKarten);
    }
}
