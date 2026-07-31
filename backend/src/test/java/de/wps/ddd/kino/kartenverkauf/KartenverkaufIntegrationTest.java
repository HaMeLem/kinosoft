package de.wps.ddd.kino.kartenverkauf;

import de.wps.ddd.kino.IntegrationTestMitWochenplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Geldbetrag;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.KartenBlock;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Preisberechnung;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Zahlungsstatus;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Platzanzahl;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Beginn;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Film;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class KartenverkaufIntegrationTest extends IntegrationTestMitWochenplan {

    @Autowired
    private AktuelleVorstellungen vorstellungen;

    @Autowired
    private SaalplanStapel saalplanStapel;

    @Autowired
    private Preisberechnung preisberechnung;

    @Autowired
    private KartenBlock kinokartenblock;

    // Basiert auf Domain Story 1: Kinokarten an der Kasse verkaufen
    @Test
    void kartenverkauf() {

        // 1. Kinobesucher sucht Vorstellung in Wochenplan aus → Vorstellung ausgesucht
        var vorstellungId = vorstellungId("The Fast and the Curious", "kleiner Saal", LocalDateTime.of(2025, 3, 23, 14, 30));
        var vorstellung = vorstellungen.holeVorstellung(vorstellungId);
        assertThat(vorstellung.getFilm()).isEqualTo(new Film("The Fast and the Curious"));
        assertThat(vorstellung.getSaal()).isEqualTo(new Saal("kleiner Saal"));
        assertThat(vorstellung.getBeginn()).isEqualTo(new Beginn(LocalDateTime.parse("2025-03-23T14:30")));

        // 2. Kinobesucher fragt nach Karten für Vorstellung → Platz-/Kartenanzahl angefragt
        var platzanzahl = new Platzanzahl(4);

        // 3. Kassenmitarbeiter holt Saalplan zu Vorstellung aus Saalplanstapel --> Saalplan geholt
        var saalplan = saalplanStapel.holeSaalplan(vorstellungId);
        assertThat(saalplan.getVorstellungId()).isEqualTo(vorstellungId);

        // 4. Kassenmitarbeiter sucht gewünschte Anzahl Plätze im Saalplan → Zusammenhängende Plätze gefunden
        // 5a. Kassenmitarbeiter bietet gefundene Plätze an → Plätze angeboten
        var vorgeschlagenePlaetze = saalplan.sucheZusammenhaengendePlaetze(platzanzahl);
        assertThat(vorgeschlagenePlaetze.anzahl()).isEqualTo(platzanzahl);
        assertThat(vorgeschlagenePlaetze.plaetze()).allMatch(platzId -> saalplan.platz(platzId).istFrei());
        assertThat(vorgeschlagenePlaetze.plaetze()).extracting(platzId -> platzId.reihe().nummer()).containsExactly(4, 4, 4, 4);
        assertThat(vorgeschlagenePlaetze.plaetze()).extracting(platzId -> platzId.platz().nummer()).containsExactly(1, 2, 3, 4);

        // 5b. Kinobesucher stimmt den Plätzen zu → Plätze gewählt
        var gewaehltePlaetze = new ZusammenhaengendePlaetze(vorgeschlagenePlaetze.plaetze().stream().toList());
        assertThat(gewaehltePlaetze.anzahl()).isEqualTo(platzanzahl);

        // 6. Kinobesucher bezahlt Geldbetrag -> Zahlung erfolgt
        var gesamtbetrag = preisberechnung.ermittlePreis(vorstellung, saalplan, gewaehltePlaetze);
        assertThat(gesamtbetrag).isEqualTo(Geldbetrag.euro(50, 0));

        var auftragsnummer = zahleGeldbetrag(vorstellungId, gewaehltePlaetze, gesamtbetrag);

        // 7. Kassenmitarbeiter markiert verkaufte Plätze im Saalplan → Plätze als verkauft markiert
        saalplan.markiereAlsVerkauft(gewaehltePlaetze);
        assertThat(gewaehltePlaetze.plaetze()).allMatch(platzId -> saalplan.platz(platzId).istVerkauft());

        // 8. Kassenmitarbeiter legt Saalplan zurück auf Saalplanstapel → Saalplan zurückgelegt
        saalplanStapel.legeZurueck(saalplan);

        // 9. Kassenmitarbeiter beschriftet Kinokarten → Kinokarten ausgestellt
        var kinokarten = kinokartenblock.erstelleKarten(auftragsnummer, vorstellung, gewaehltePlaetze);
        assertThat(kinokarten).hasSize(platzanzahl.value());
        assertThat(kinokarten).allSatisfy(kinokarte -> {
            assertThat(kinokarte.getFilm()).isEqualTo(vorstellung.getFilm());
            assertThat(kinokarte.getBeginn()).isEqualTo(vorstellung.getBeginn());
            assertThat(kinokarte.getSaal()).isEqualTo(vorstellung.getSaal());
            assertThat(kinokarte.getReihe().nummer()).isEqualTo(4);
        });
        assertThat(kinokarten).extracting(k -> k.getPlatz().nummer()).containsExactly(1, 2, 3, 4);

        // 10. Kassenmitarbeiter übergibt fertige Kinokarten → Kinokarten übergeben/verkauft
        assertThat(kinokarten).allSatisfy(kinokarte -> {
            assertThat(kinokarte.getAuftragsnummer()).isEqualTo(auftragsnummer);
            assertThat(kinokarte.getVorstellungId()).isEqualTo(vorstellungId);
        });
        assertThat(kinokarten).extracting(Kinokarte::getId).doesNotHaveDuplicates();
    }

    private static @NonNull Auftragsnummer zahleGeldbetrag(VorstellungId vorstellungId, ZusammenhaengendePlaetze gewaehltePlaetze, Geldbetrag geldbetrag) {
        var verkaufsvorgang = Verkaufsvorgang.starte(vorstellungId, gewaehltePlaetze, geldbetrag);
        var zahlungsvorgang = verkaufsvorgang.starteZahlungsvorgang();
        assertThat(zahlungsvorgang.getBetrag()).isEqualTo(geldbetrag);
        assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Ausstehend);

        verkaufsvorgang.zahlungEingegangen(zahlungsvorgang.getId());
        assertThat(zahlungsvorgang.getStatus()).isEqualTo(Zahlungsstatus.Eingegangen);
        return verkaufsvorgang.getAuftragsnummer();
    }

}
