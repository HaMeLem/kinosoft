package de.wps.ddd.kino.zahlung.web;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.common.web.GlobalExceptionHandler;
import de.wps.ddd.kino.zahlung.application.Zahlungsabwicklung;
import de.wps.ddd.kino.zahlung.domain.Betrag;
import de.wps.ddd.kino.zahlung.domain.Zahlung;
import de.wps.ddd.kino.zahlung.domain.Zahlungen;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ZahlungsdienstleisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, ZahlungsdienstleisterDtoMapper.class})
class ZahlungsdienstleisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Zahlungen zahlungen;
    @MockitoBean
    private Zahlungsabwicklung zahlungsabwicklung;

    private static final UUID REFERENZ = UUID.randomUUID();

    @Test
    void hole_bekannteZahlung_liefertZahlungDto() throws Exception {
        // arrange
        var referenz = new Zahlungsreferenz(REFERENZ);
        when(zahlungen.hole(referenz)).thenReturn(Zahlung.fuer(referenz, new Betrag(5000)));

        // act / assert
        mockMvc.perform(get("/api/zahlung/{referenz}", REFERENZ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenz").value(REFERENZ.toString()))
                .andExpect(jsonPath("$.betragInCent").value(5000))
                .andExpect(jsonPath("$.status").value("Offen"));
    }

    @Test
    void hole_unbekannteReferenz_liefert404() throws Exception {
        // arrange
        var referenz = new Zahlungsreferenz(REFERENZ);
        when(zahlungen.hole(referenz)).thenAnswer(invocation -> {
            RessourceNichtGefunden.wenn(true, "Zahlung " + REFERENZ + " existiert nicht");
            return null;
        });

        // act / assert
        mockMvc.perform(get("/api/zahlung/{referenz}", REFERENZ))
                .andExpect(status().isNotFound());
    }

    @Test
    void bezahle_bekannteZahlung_stoesstAbwicklungAnUndLiefert202() throws Exception {
        // arrange
        var referenz = new Zahlungsreferenz(REFERENZ);
        when(zahlungen.hole(referenz)).thenReturn(Zahlung.fuer(referenz, new Betrag(5000)));

        // act
        mockMvc.perform(post("/api/zahlung/{referenz}/bezahlen", REFERENZ))
                .andExpect(status().isAccepted());

        // assert
        verify(zahlungsabwicklung).bezahle(referenz);
    }
}
