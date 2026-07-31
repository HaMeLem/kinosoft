package de.wps.ddd.kino.kartenverkauf.adapters.persistence.mappers;

import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers.VerkaufsvorgangEntityMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model.VerkaufsvorgangEntity;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgangstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VerkaufsvorgangEntityMapperTest {

    private final VerkaufsvorgangEntityMapper mapper = new VerkaufsvorgangEntityMapper();

    private final Auftragsnummer auftragsnummer =
            new Auftragsnummer(UUID.fromString("a095c8f6-6fa2-4f2e-acf1-52cee0698e74"));
    private final VorstellungId vorstellungId =
            new VorstellungId(UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90"));
    private final ZahlungsvorgangId zahlungsvorgangId =
            new ZahlungsvorgangId(UUID.fromString("0d1b5e1a-2c3d-4e5f-8a9b-0c1d2e3f4a5b"));

    @Test
    void toEntity() {
        // arrange
        var verkaufsvorgang = new Verkaufsvorgang(auftragsnummer, vorstellungId,
                new ZusammenhaengendePlaetze(List.of(platzId(4, 1), platzId(4, 2))),
                Geldbetrag.euro(25, 0),
                new Zahlungsvorgang(zahlungsvorgangId, 2, Geldbetrag.euro(25, 0), Zahlungsstatus.Eingegangen),
                2, Verkaufsvorgangstatus.Abgeschlossen);

        // act
        var entity = mapper.toEntity(verkaufsvorgang);

        // assert
        assertThat(entity.getAuftragsnummer()).isEqualTo(auftragsnummer.nummer());
        assertThat(entity.getVorstellungId()).isEqualTo(vorstellungId.uuid());
        assertThat(entity.getGesamtpreisInCent()).isEqualTo(2500);
        assertThat(entity.getStatus()).isEqualTo("Abgeschlossen");
        assertThat(entity.getPlaetze()).extracting(VerkaufsvorgangEntity.PlatzEmbeddable::getPlatz)
                .containsExactly(1, 2);
        assertThat(entity.getPlaetze()).allSatisfy(platz -> assertThat(platz.getReihe()).isEqualTo(4));
        assertThat(entity.getAnlaeufe()).isEqualTo(2);
        assertThat(entity.getZahlungsvorgang().getZahlungsvorgangId()).isEqualTo(zahlungsvorgangId.wert());
        assertThat(entity.getZahlungsvorgang().getAnlauf()).isEqualTo(2);
        assertThat(entity.getZahlungsvorgang().getBetragInCent()).isEqualTo(2500);
        assertThat(entity.getZahlungsvorgang().getStatus()).isEqualTo("Eingegangen");
    }

    @Test
    void toDomain() {
        // arrange
        var entity = new VerkaufsvorgangEntity(auftragsnummer.nummer(), vorstellungId.uuid(), List.of(
                new VerkaufsvorgangEntity.PlatzEmbeddable(4, 1),
                new VerkaufsvorgangEntity.PlatzEmbeddable(4, 2)), 2500,
                new VerkaufsvorgangEntity.ZahlungsvorgangEmbeddable(zahlungsvorgangId.wert(), 1, 2500, "Ausstehend"),
                1, "Laufend");

        // act
        var verkaufsvorgang = mapper.toDomain(entity);

        // assert
        assertThat(verkaufsvorgang.getAuftragsnummer()).isEqualTo(auftragsnummer);
        assertThat(verkaufsvorgang.getVorstellungId()).isEqualTo(vorstellungId);
        assertThat(verkaufsvorgang.getGesamtpreis()).isEqualTo(Geldbetrag.euro(25, 0));
        assertThat(verkaufsvorgang.getStatus()).isEqualTo(Verkaufsvorgangstatus.Laufend);
        assertThat(verkaufsvorgang.getGewaehltePlaetze().plaetze())
                .containsExactly(platzId(4, 1), platzId(4, 2));
        assertThat(verkaufsvorgang.getAnlaeufe()).isEqualTo(1);
        assertThat(verkaufsvorgang.zahlungsvorgang()).get().satisfies(zahlungsvorgang -> {
            assertThat(zahlungsvorgang.getId()).isEqualTo(zahlungsvorgangId);
            assertThat(zahlungsvorgang.getAnlauf()).isEqualTo(1);
            assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Ausstehend);
        });
    }

    private static PlatzId platzId(int reihe, int platz) {
        return new PlatzId(new ReiheNummer(reihe), new PlatzNummer(platz));
    }
}
