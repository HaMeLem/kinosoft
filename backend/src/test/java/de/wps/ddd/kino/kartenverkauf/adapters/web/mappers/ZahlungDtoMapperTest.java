package de.wps.ddd.kino.kartenverkauf.adapters.web.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.SaalplanDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.ZahlungDtoMapper;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgangstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ZahlungDtoMapperTest {

    private final ZahlungDtoMapper mapper = new ZahlungDtoMapper(new SaalplanDtoMapper());

    @Test
    void toDto_verkaufsvorgang_mapptAuftragsnummerPlaetzeUndGesamtpreis() {
        // arrange
        var verkaufsvorgang = new Verkaufsvorgang(
                new Auftragsnummer(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74")),
                new VorstellungId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90")),
                new ZusammenhaengendePlaetze(List.of(new PlatzId(new ReiheNummer(4), new PlatzNummer(1)))),
                Geldbetrag.euro(12, 50),
                null,
                0,
                Verkaufsvorgangstatus.Laufend);

        // act
        var dto = mapper.toDto(verkaufsvorgang);

        // assert
        assertThat(dto.auftragsnummer()).isEqualTo("a095c8f6-6fa2-4f2e-acf1-52cee0698e74");
        assertThat(dto.vorstellungId()).isEqualTo("090c173a-3636-4980-865a-1ec859eb4f90");
        assertThat(dto.gesamtpreis().betrag()).isEqualTo(1250);
        assertThat(dto.plaetze().plaetze()).singleElement()
                .satisfies(platz -> assertThat(platz.reihe()).isEqualTo(4));
    }

    @Test
    void toDto_geldbetrag_mapptBetragUndWaehrung() {
        // act
        var dto = mapper.toDto(Geldbetrag.euro(10, 50));

        // assert
        assertThat(dto.betrag()).isEqualTo(1050);
        assertThat(dto.waehrung()).isEqualTo("EUR");
    }

    @Test
    void toDto_zahlungsstatus_mapptStatusAlsString() {
        // act
        var dto = mapper.toDto(Zahlungsstatus.Eingegangen);

        // assert
        assertThat(dto.status()).isEqualTo("Eingegangen");
    }
}
