package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.controllers;

import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.KartenDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.SaalplanDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.VorstellungDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.ZahlungDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.GeldbetragDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.KartenbestellungDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.KinokarteDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.PreisanfrageDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.SaalplanDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.VerkaufsvorgangDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.VorstellungDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.ZahlungsstatusDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.ZahlungsvorgangDto;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.model.ZusammenhaengendePlaetzeDto;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platzanzahl;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.BerechneGesamtpreis;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleGewaehlteVorstellung;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleKinokarten;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleSaalplan;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleZahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteVerkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteZahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.SucheZusammenhaengendePlaetze;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

/** Die Methoden stehen in der Reihenfolge des Kaufflusses. */
@Slf4j
@RestController
@RequestMapping("/api/kartenverkauf")
@RequiredArgsConstructor
class KartenverkaufController {

    private final VorstellungDtoMapper vorstellungMapper;
    private final SaalplanDtoMapper saalplanMapper;
    private final ZahlungDtoMapper zahlungMapper;
    private final KartenDtoMapper kartenMapper;

    private final HoleGewaehlteVorstellung holeGewaehlteVorstellung;
    private final HoleSaalplan holeSaalplan;
    private final SucheZusammenhaengendePlaetze sucheZusammenhaengendePlaetze;
    private final BerechneGesamtpreis berechneGesamtpreis;
    private final StarteVerkaufsvorgang starteVerkaufsvorgang;
    private final StarteZahlungsvorgang starteZahlungsvorgang;
    private final HoleZahlungsstatus holeZahlungsstatus;
    private final HoleKinokarten holeKinokarten;

    @GetMapping("/vorstellungen/{vorstellungId}")
    public VorstellungDto holeGewaehlteVorstellung(@PathVariable UUID vorstellungId) {
        var vorstellung = holeGewaehlteVorstellung.fuer(new VorstellungId(vorstellungId));
        return vorstellungMapper.toDto(vorstellung);
    }

    @GetMapping("/saalplaene/{vorstellungId}")
    public SaalplanDto holeSaalplan(@PathVariable UUID vorstellungId) {
        var saalplan = holeSaalplan.fuer(new VorstellungId(vorstellungId));
        return saalplanMapper.toDto(saalplan);
    }

    @GetMapping("/saalplaene/{vorstellungId}/suche-zusammenhaengende-plaetze")
    public ZusammenhaengendePlaetzeDto sucheZusammenhaengendePlaetze(@PathVariable UUID vorstellungId, @RequestParam int platzanzahl) {
        var anzahl = new Platzanzahl(platzanzahl);
        var plaetze = sucheZusammenhaengendePlaetze.fuer(new VorstellungId(vorstellungId), anzahl);
        return saalplanMapper.toDto(plaetze);
    }

    @PostMapping("/preisanfrage")
    public GeldbetragDto berechneGesamtpreis(@RequestBody PreisanfrageDto preisanfrageDto) {
        var vorstellungId = new VorstellungId(preisanfrageDto.vorstellungId());
        var gewaehltePlaetze = saalplanMapper.toDomain(preisanfrageDto.plaetze());
        var gesamtpreis = berechneGesamtpreis.fuer(vorstellungId, gewaehltePlaetze);
        return zahlungMapper.toDto(gesamtpreis);
    }

    @PostMapping("/verkaufsvorgaenge")
    public ResponseEntity<VerkaufsvorgangDto> starteVerkaufsvorgang(@RequestBody KartenbestellungDto bestellung) {
        var vorstellungId = new VorstellungId(bestellung.vorstellungId());
        var gewaehltePlaetze = saalplanMapper.toDomain(bestellung.plaetze());

        var verkaufsvorgang = starteVerkaufsvorgang.fuer(vorstellungId, gewaehltePlaetze);

        var dto = zahlungMapper.toDto(verkaufsvorgang);
        var ort = UriComponentsBuilder.fromPath("/api/kartenverkauf/verkaufsvorgaenge/{auftragsnummer}")
                .buildAndExpand(dto.auftragsnummer()).toUri();
        return ResponseEntity.created(ort).body(dto);
    }

    @PostMapping("/verkaufsvorgaenge/{auftragsnummer}/zahlungsvorgaenge")
    public ResponseEntity<ZahlungsvorgangDto> starteZahlungsvorgang(@PathVariable UUID auftragsnummer) {
        var zahlungsvorgang = starteZahlungsvorgang.fuer(new Auftragsnummer(auftragsnummer));

        var dto = zahlungMapper.toDto(zahlungsvorgang);
        var ort = UriComponentsBuilder
                .fromPath("/api/kartenverkauf/verkaufsvorgaenge/{auftragsnummer}/zahlungsvorgaenge/{id}")
                .buildAndExpand(auftragsnummer, dto.id()).toUri();
        return ResponseEntity.created(ort).body(dto);
    }

    @GetMapping("/verkaufsvorgaenge/{auftragsnummer}/zahlungsstatus")
    public ZahlungsstatusDto holeZahlungsstatus(@PathVariable UUID auftragsnummer) {
        var status = holeZahlungsstatus.fuer(new Auftragsnummer(auftragsnummer));
        return zahlungMapper.toDto(status);
    }

    @GetMapping("/verkaufsvorgaenge/{auftragsnummer}/kinokarten")
    public List<KinokarteDto> holeKinokarten(@PathVariable UUID auftragsnummer) {
        var kinokarten = holeKinokarten.fuer(new Auftragsnummer(auftragsnummer));
        return kartenMapper.toDto(kinokarten);
    }
}
