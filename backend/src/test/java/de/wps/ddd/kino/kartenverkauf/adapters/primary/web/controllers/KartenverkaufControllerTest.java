package de.wps.ddd.kino.kartenverkauf.adapters.primary.web.controllers;

import de.wps.ddd.kino.common.web.GlobalExceptionHandler;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.KartenDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.SaalplanDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.VorstellungDtoMapper;
import de.wps.ddd.kino.kartenverkauf.adapters.primary.web.mappers.ZahlungDtoMapper;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KinokarteId;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgangstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platz;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzId;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzKategorie;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.PlatzNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platzanzahl;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ReiheNummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungKategorie;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.BerechneGesamtpreis;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleGewaehlteVorstellung;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleKinokarten;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleSaalplan;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.HoleZahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteVerkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.StarteZahlungsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.ports.primary.SucheZusammenhaengendePlaetze;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = KartenverkaufController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({VorstellungDtoMapper.class, SaalplanDtoMapper.class, ZahlungDtoMapper.class, KartenDtoMapper.class,
        GlobalExceptionHandler.class})
class KartenverkaufControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String PREISANFRAGE_JSON = """
            {"vorstellungId":"090c173a-3636-4980-865a-1ec859eb4f90",
             "plaetze":{"plaetze":[{"reihe":4,"platz":1},{"reihe":4,"platz":2}]}}""";

    @MockitoBean
    private HoleGewaehlteVorstellung holeGewaehlteVorstellung;
    @MockitoBean
    private HoleSaalplan holeSaalplan;
    @MockitoBean
    private SucheZusammenhaengendePlaetze sucheZusammenhaengendePlaetze;
    @MockitoBean
    private BerechneGesamtpreis berechneGesamtpreis;
    @MockitoBean
    private StarteVerkaufsvorgang starteVerkaufsvorgang;
    @MockitoBean
    private StarteZahlungsvorgang starteZahlungsvorgang;
    @MockitoBean
    private HoleZahlungsstatus holeZahlungsstatus;
    @MockitoBean
    private HoleKinokarten holeKinokarten;

    private static final UUID VORSTELLUNG_ID = UUID.fromString("090c173a-3636-4980-865a-1ec859eb4f90");
    private static final UUID AUFTRAGSNUMMER = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void holeGewaehlteVorstellung_liefertVorstellungDto() throws Exception {
        // arrange
        when(holeGewaehlteVorstellung.fuer(new VorstellungId(VORSTELLUNG_ID))).thenReturn(vorstellung());

        // act / assert
        mockMvc.perform(get("/api/kartenverkauf/vorstellungen/{vorstellungId}", VORSTELLUNG_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.film").value("The Fast and the Curious"))
                .andExpect(jsonPath("$.saal").value("kleiner Saal"));
    }

    @Test
    void holeSaalplan_liefertSaalplanDto() throws Exception {
        // arrange
        var saalplan = new Saalplan(new VorstellungId(VORSTELLUNG_ID), List.of(
                new Platz(platzId(1, 1), PlatzKategorie.Parkett, false)));
        when(holeSaalplan.fuer(new VorstellungId(VORSTELLUNG_ID))).thenReturn(saalplan);

        // act / assert
        mockMvc.perform(get("/api/kartenverkauf/saalplaene/{vorstellungId}", VORSTELLUNG_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plaetze[0][0].reihe").value(1))
                .andExpect(jsonPath("$.plaetze[0][0].istFrei").value(true));
    }

    @Test
    void sucheZusammenhaengendePlaetze_liefertGefundenePlaetze() throws Exception {
        // arrange
        when(sucheZusammenhaengendePlaetze.fuer(new VorstellungId(VORSTELLUNG_ID), new Platzanzahl(2)))
                .thenReturn(new ZusammenhaengendePlaetze(List.of(platzId(4, 1), platzId(4, 2))));

        // act / assert
        mockMvc.perform(get("/api/kartenverkauf/saalplaene/{vorstellungId}/suche-zusammenhaengende-plaetze", VORSTELLUNG_ID)
                        .param("platzanzahl", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plaetze.length()").value(2))
                .andExpect(jsonPath("$.plaetze[0].reihe").value(4))
                .andExpect(jsonPath("$.plaetze[0].platz").value(1));
    }

    @Test
    void sucheZusammenhaengendePlaetze_negativePlatzanzahl_liefert400() throws Exception {
        // act / assert
        mockMvc.perform(get("/api/kartenverkauf/saalplaene/{vorstellungId}/suche-zusammenhaengende-plaetze", VORSTELLUNG_ID)
                        .param("platzanzahl", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void berechneGesamtpreis_liefertGeldbetragDto() throws Exception {
        // arrange
        when(berechneGesamtpreis.fuer(eq(new VorstellungId(VORSTELLUNG_ID)), any())).thenReturn(Geldbetrag.euro(50, 0));
        var body = PREISANFRAGE_JSON;

        // act / assert
        mockMvc.perform(post("/api/kartenverkauf/preisanfrage").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.betrag").value(5000))
                .andExpect(jsonPath("$.waehrung").value("EUR"));
    }

    private static Vorstellung vorstellung() {
        return new Vorstellung(
                new VorstellungId(VORSTELLUNG_ID),
                new Saal("kleiner Saal"),
                new Beginn(LocalDateTime.parse("2025-03-23T14:30")),
                new Film("The Fast and the Curious"),
                VorstellungKategorie.Standard);
    }

    private static PlatzId platzId(int reihe, int platz) {
        return new PlatzId(new ReiheNummer(reihe), new PlatzNummer(platz));
    }

    @Test
    void starteVerkaufsvorgang_liefertVorgangMitAuftragsnummerUndGesamtpreis() throws Exception {
        // arrange
        when(starteVerkaufsvorgang.fuer(any(), any())).thenReturn(verkaufsvorgang());
        var body = """
                {"vorstellungId":"%s","plaetze":{"plaetze":[{"reihe":4,"platz":1}]}}""".formatted(VORSTELLUNG_ID);

        // act / assert
        mockMvc.perform(post("/api/kartenverkauf/verkaufsvorgaenge").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/kartenverkauf/verkaufsvorgaenge/" + AUFTRAGSNUMMER))
                .andExpect(jsonPath("$.auftragsnummer").value(AUFTRAGSNUMMER.toString()))
                .andExpect(jsonPath("$.vorstellungId").value(VORSTELLUNG_ID.toString()))
                .andExpect(jsonPath("$.gesamtpreis.betrag").value(5000))
                .andExpect(jsonPath("$.plaetze.plaetze[0].reihe").value(4));
    }

    @Test
    void starteZahlungsvorgang_liefertDenNeuenZahlungsvorgangUndBrauchtKeinenBody() throws Exception {
        // arrange
        var zahlungsvorgang = verkaufsvorgang().starteZahlungsvorgang();
        when(starteZahlungsvorgang.fuer(new Auftragsnummer(AUFTRAGSNUMMER))).thenReturn(zahlungsvorgang);

        // act / assert
        mockMvc.perform(post("/api/kartenverkauf/verkaufsvorgaenge/{auftragsnummer}/zahlungsvorgaenge", AUFTRAGSNUMMER))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "/api/kartenverkauf/verkaufsvorgaenge/" + AUFTRAGSNUMMER + "/zahlungsvorgaenge/" + zahlungsvorgang.getId().wert()))
                .andExpect(jsonPath("$.id").value(zahlungsvorgang.getId().wert().toString()))
                .andExpect(jsonPath("$.anlauf").value(1))
                .andExpect(jsonPath("$.betrag.betrag").value(5000))
                .andExpect(jsonPath("$.status").value("Ausstehend"));

        verify(starteZahlungsvorgang).fuer(new Auftragsnummer(AUFTRAGSNUMMER));
    }

    @Test
    void holeZahlungsstatus_liefertStatusDto() throws Exception {
        // arrange
        when(holeZahlungsstatus.fuer(new Auftragsnummer(AUFTRAGSNUMMER))).thenReturn(Zahlungsstatus.Eingegangen);

        // act / assert
        mockMvc.perform(get("/api/kartenverkauf/verkaufsvorgaenge/{auftragsnummer}/zahlungsstatus", AUFTRAGSNUMMER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Eingegangen"));
    }

    @Test
    void holeKinokarten_liefertKinokartenDtos() throws Exception {
        // arrange
        when(holeKinokarten.fuer(new Auftragsnummer(AUFTRAGSNUMMER))).thenReturn(List.of(kinokarte()));

        // act / assert
        mockMvc.perform(get("/api/kartenverkauf/verkaufsvorgaenge/{auftragsnummer}/kinokarten", AUFTRAGSNUMMER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].film").value("The Fast and the Curious"))
                .andExpect(jsonPath("$[0].reihe").value(4))
                .andExpect(jsonPath("$[0].platz").value(1));
    }

    private static Verkaufsvorgang verkaufsvorgang() {
        return new Verkaufsvorgang(
                new Auftragsnummer(AUFTRAGSNUMMER),
                new VorstellungId(VORSTELLUNG_ID),
                new ZusammenhaengendePlaetze(List.of(new PlatzId(new ReiheNummer(4), new PlatzNummer(1)))),
                Geldbetrag.euro(50, 0),
                null,
                0,
                de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgangstatus.Laufend);
    }

    private static Kinokarte kinokarte() {
        return new Kinokarte(
                KinokarteId.neu(),
                new Auftragsnummer(AUFTRAGSNUMMER),
                new VorstellungId(VORSTELLUNG_ID),
                new Film("The Fast and the Curious"),
                new Beginn(LocalDateTime.parse("2025-03-23T14:30")),
                new Saal("kleiner Saal"),
                new ReiheNummer(4),
                new PlatzNummer(1));
    }
}
