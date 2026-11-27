package de.wps.ddd.kino.common.domain;

import java.time.LocalTime;

/* Das Kino öffnet an jedem Wochentag um 8 Uhr und schließt am Folgetag um 3 Uhr */

public enum Kinooeffnungszeiten {

    MONTAG(LocalTime.of(8,00), LocalTime.of(3,00)),
    DIENSTAG(LocalTime.of(8,00), LocalTime.of(3,00)),
    MITTWOCH(LocalTime.of(8,00), LocalTime.of(3,00)),
    DONNERSTAG(LocalTime.of(8,00), LocalTime.of(3,00)),
    FREITAG(LocalTime.of(8,00), LocalTime.of(3,00)),
    SAMSTAG(LocalTime.of(8,00), LocalTime.of(3,00)),
    SONNTAG(LocalTime.of(8,00), LocalTime.of(3,00));

    private final LocalTime oeffnet;
    private final LocalTime schliesst;

    Kinooeffnungszeiten(LocalTime oeffnet, LocalTime schliesst) {
        this.oeffnet = oeffnet;
        this.schliesst = schliesst;
    }

    public LocalTime oeffnet() {
        return oeffnet;
    }

    public LocalTime schliesst() {
        return schliesst;
    }
}