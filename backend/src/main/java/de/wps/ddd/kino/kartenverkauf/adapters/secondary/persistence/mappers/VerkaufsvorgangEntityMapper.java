package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.mappers;

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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VerkaufsvorgangEntityMapper {

    public VerkaufsvorgangEntity toEntity(Verkaufsvorgang verkaufsvorgang) {
        var plaetze = verkaufsvorgang.getGewaehltePlaetze().plaetze().stream()
                .map(platzId -> new VerkaufsvorgangEntity.PlatzEmbeddable(
                        platzId.reihe().nummer(), platzId.platz().nummer()))
                .toList();
        return new VerkaufsvorgangEntity(
                verkaufsvorgang.getAuftragsnummer().nummer(),
                verkaufsvorgang.getVorstellungId().uuid(),
                new ArrayList<>(plaetze),
                verkaufsvorgang.getGesamtpreis().getBetrag(),
                toEntity(verkaufsvorgang.zahlungsvorgang().orElse(null)),
                verkaufsvorgang.getAnlaeufe(),
                verkaufsvorgang.getStatus().name()
        );
    }

    public Verkaufsvorgang toDomain(VerkaufsvorgangEntity verkaufsvorgangEntity) {
        var plaetze = verkaufsvorgangEntity.getPlaetze().stream()
                .map(platz -> new PlatzId(new ReiheNummer(platz.getReihe()), new PlatzNummer(platz.getPlatz())))
                .toList();
        return new Verkaufsvorgang(
                new Auftragsnummer(verkaufsvorgangEntity.getAuftragsnummer()),
                new VorstellungId(verkaufsvorgangEntity.getVorstellungId()),
                new ZusammenhaengendePlaetze(plaetze),
                Geldbetrag.euroInCent(verkaufsvorgangEntity.getGesamtpreisInCent()),
                toDomain(verkaufsvorgangEntity.getZahlungsvorgang()),
                verkaufsvorgangEntity.getAnlaeufe(),
                Verkaufsvorgangstatus.valueOf(verkaufsvorgangEntity.getStatus())
        );
    }

    private VerkaufsvorgangEntity.ZahlungsvorgangEmbeddable toEntity(Zahlungsvorgang zahlungsvorgang) {
        if (zahlungsvorgang == null) {
            return null;
        }
        return new VerkaufsvorgangEntity.ZahlungsvorgangEmbeddable(
                zahlungsvorgang.getId().wert(),
                zahlungsvorgang.getAnlauf(),
                zahlungsvorgang.getBetrag().getBetrag(),
                zahlungsvorgang.getStatus().name());
    }

    private Zahlungsvorgang toDomain(VerkaufsvorgangEntity.ZahlungsvorgangEmbeddable zahlungsvorgang) {
        if (zahlungsvorgang == null || zahlungsvorgang.getZahlungsvorgangId() == null) {
            return null;
        }
        return new Zahlungsvorgang(
                new ZahlungsvorgangId(zahlungsvorgang.getZahlungsvorgangId()),
                zahlungsvorgang.getAnlauf(),
                Geldbetrag.euroInCent(zahlungsvorgang.getBetragInCent()),
                Zahlungsstatus.valueOf(zahlungsvorgang.getStatus()));
    }
}
