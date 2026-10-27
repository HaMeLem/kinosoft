package de.wps.ddd.kino.einlass.service;

import java.util.List;
import java.util.UUID;

/* Klasse VerkaufteKinokarten 
ist ein Value Object, das die verkauften Kinokarten für eine Vorstellung 
repräsentiert. Sie enthält die VorstellungId, den Saal und 
eine Liste von Karten, die jeweils eine KartenId, 
eine Reihe und einen Platz enthalten.
 */

public record VerkaufteKinokarten(UUID vorstellungId,String saal,List<Karte> karten) 
{
    public record Karte(UUID kartenId, int reihe, int platz) 
    {
         /* die innere Klasse Karte repräsentiert eine einzelne verkaufte Kinokarte mit
    einer KartenId, einer Reihe und einem Platz. Sie wird in der Liste karten
    innerhalb des Value Objects VerkaufteKinokarten verwendet. */
    }
}
