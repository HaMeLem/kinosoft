package de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.ZusammenhaengendePlaetze;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import lombok.AccessLevel;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.Optional;

@AggregateRoot
@Getter
public class Verkaufsvorgang {

    @Identity
    private final Auftragsnummer auftragsnummer;
    private final VorstellungId vorstellungId;
    private final ZusammenhaengendePlaetze gewaehltePlaetze;
    private final Geldbetrag gesamtpreis;

    @Getter(AccessLevel.NONE)
    private Zahlungsvorgang zahlungsvorgang;
    private int anlaeufe;
    private Verkaufsvorgangstatus status;

    public Verkaufsvorgang(Auftragsnummer auftragsnummer, VorstellungId vorstellungId,
                           ZusammenhaengendePlaetze gewaehltePlaetze, Geldbetrag gesamtpreis,
                           Zahlungsvorgang zahlungsvorgang, int anlaeufe, Verkaufsvorgangstatus status) {
        this.auftragsnummer = auftragsnummer;
        this.vorstellungId = vorstellungId;
        this.gewaehltePlaetze = gewaehltePlaetze;
        this.gesamtpreis = gesamtpreis;
        this.zahlungsvorgang = zahlungsvorgang;
        this.anlaeufe = anlaeufe;
        this.status = status;
    }

    public static Verkaufsvorgang starte(VorstellungId vorstellungId,
                                         ZusammenhaengendePlaetze gewaehltePlaetze,
                                         Geldbetrag gesamtpreis) {
        return new Verkaufsvorgang(Auftragsnummer.neueAuftragsnummer(), vorstellungId, gewaehltePlaetze,
                gesamtpreis, null, 0, Verkaufsvorgangstatus.Laufend);
    }

    public Zahlungsvorgang starteZahlungsvorgang() {
        GeschaeftsregelVerletzt.wenn(!istLaufend(),
                "Ein abgeschlossener Verkaufsvorgang kann nicht erneut bezahlt werden.");
        GeschaeftsregelVerletzt.wenn(zahlungsvorgang != null && zahlungsvorgang.istOffen(),
                "Es läuft schon ein Zahlungsvorgang.");

        this.anlaeufe++;
        this.zahlungsvorgang = Zahlungsvorgang.starte(anlaeufe, gesamtpreis);
        return zahlungsvorgang;
    }

    public void zahlungEingegangen(ZahlungsvorgangId zahlungsvorgangId) {
        zahlungsvorgang(zahlungsvorgangId).zahlungEingegangen();
    }

    public void zahlungAbgebrochen(ZahlungsvorgangId zahlungsvorgangId) {
        zahlungsvorgang(zahlungsvorgangId).zahlungAbgebrochen();
    }

    public void schliesseAb() {
        GeschaeftsregelVerletzt.wenn(!istBezahlt(), "Zahlung ist noch nicht eingegangen");
        GeschaeftsregelVerletzt.wenn(!istLaufend(),
                "Nur laufende Verkaufsvorgänge können abgeschlossen werden.");
        this.status = Verkaufsvorgangstatus.Abgeschlossen;
    }

    public boolean istLaufend() {
        return this.status == Verkaufsvorgangstatus.Laufend;
    }

    public boolean istBezahlt() {
        return zahlungsvorgang != null && zahlungsvorgang.istEingegangen();
    }

    public Optional<Zahlungsvorgang> zahlungsvorgang() {
        return Optional.ofNullable(zahlungsvorgang);
    }

    private Zahlungsvorgang zahlungsvorgang(ZahlungsvorgangId zahlungsvorgangId) {
        RessourceNichtGefunden.wenn(zahlungsvorgang == null || !zahlungsvorgang.getId().equals(zahlungsvorgangId),
                "Zahlungsvorgang " + zahlungsvorgangId + " ist nicht der aktuelle Zahlungsvorgang zu Auftrag "
                        + auftragsnummer);
        return zahlungsvorgang;
    }
}
