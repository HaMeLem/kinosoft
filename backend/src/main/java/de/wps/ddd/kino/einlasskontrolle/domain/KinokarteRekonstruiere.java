package de.wps.ddd.kino.einlasskontrolle.domain;

public class KinokarteRekonstruiere {

}
public static Kinokarte rekonstruiere(KinokartenId id, Reihe reihe, Sitzplatz sitzplatz,
                                      Saal saal, VorstellungsId vorstellung,
                                      Entwertungszeitpunkt entwertungszeitpunkt) {
    Kinokarte karte = new Kinokarte(id, reihe, sitzplatz, saal, vorstellung);
    karte.entwertungszeitpunkt = entwertungszeitpunkt;
    return karte;
}