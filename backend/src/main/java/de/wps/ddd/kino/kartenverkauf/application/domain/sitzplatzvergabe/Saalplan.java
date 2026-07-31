package de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe;

import de.wps.ddd.kino.common.error.GeschaeftsregelVerletzt;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.Vorstellung;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Association;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AggregateRoot
public class Saalplan {

    @Identity
    @Association(aggregateType = Vorstellung.class)
    private final VorstellungId vorstellungId;
    private final TreeMap<ReiheNummer, TreeMap<PlatzNummer, Platz>> plaetze;

    public Saalplan(VorstellungId vorstellungId, List<Platz> plaetze) {
        Assert.notNull(vorstellungId, "vorstellungId must not be null");
        this.vorstellungId = vorstellungId;
        this.plaetze = plaetze.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getId().reihe(),
                        TreeMap::new, // outer map - sorted by Reihennummer
                        Collectors.toMap(
                                p -> p.getId().platz(),
                                Function.identity(),
                                (p1, p2) -> p1, // handle duplicate keys if needed
                                TreeMap::new    // inner map - sorted by Platznummer
                        )
                ));
    }

    /**
     * Erzeugt einen leeren Saalplan für die Bestuhlung eines Saals: alle Plätze frei, die vordere
     * Hälfte der Reihen Parkett, die hintere Loge.
     */
    public static Saalplan leer(VorstellungId vorstellungId, int reihen, int plaetzeProReihe) {
        var plaetze = new ArrayList<Platz>(reihen * plaetzeProReihe);
        for (int reihe = 1; reihe <= reihen; reihe++) {
            var kategorie = reihe <= reihen / 2 ? PlatzKategorie.Parkett : PlatzKategorie.Loge;
            for (int platz = 1; platz <= plaetzeProReihe; platz++) {
                plaetze.add(new Platz(new PlatzId(new ReiheNummer(reihe), new PlatzNummer(platz)), kategorie, false));
            }
        }
        return new Saalplan(vorstellungId, plaetze);
    }

    /**
     * @param anzahlPlaetze die Anzahl der gewünschten freien zusammenhängenden Plätze
     * @return die ersten freien zusammenhängenden Plätze startend von der hintersten Reihe oder eine leere Liste, wenn es keine anzahlPlaetze zusammenhängenden Plätze gibt
     */
    public ZusammenhaengendePlaetze sucheZusammenhaengendePlaetze(Platzanzahl anzahlPlaetze) {
        GeschaeftsregelVerletzt.wenn(anzahlPlaetze.value() < 1, "Mindestens ein Platz muss angefragt werden.");
        var result = new ArrayList<PlatzId>();
        for (var reihe : plaetze.descendingMap().values()) {
            for (Platz platz : reihe.values()) {
                if (platz.istFrei()) {
                    result.add(platz.getId());
                    if (result.size() == anzahlPlaetze.value()) {
                        return new ZusammenhaengendePlaetze(result);
                    }
                } else {
                    result.clear();
                }
            }
            result.clear();
        }
        return new ZusammenhaengendePlaetze(result);
    }

    public void markiereAlsVerkauft(ZusammenhaengendePlaetze zusammenhaengendePlaetze) {
        for (PlatzId p : zusammenhaengendePlaetze.plaetze()) {
            platz(p).markiereAlsVerkauft();
        }
    }

    public Platz platz(PlatzId id) {
        return this.plaetze.get(id.reihe()).get(id.platz());
    }

}
