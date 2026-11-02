package de.wps.ddd.kino.einlasskontrolle.domain;


// hier wird das neue Java 17 Feature "Sealed Interfaces" verwendet, um die möglichen Rückgabewerte der Methode Kinokarte.entwerten() zu beschreiben.
// Die Methode entwertet findet sich in der Kinokarte-Klasse. Sie prüft, ob die Kinokarte entwertet werden kann und gibt entweder
//  eine Erlaubnis oder eine Liste von Ablehnungsgründen zurück.

public sealed interface Einlassentscheidung {

    // Erlaubt: Die Kinokarte wurde erfolgreich entwertet, der Gast darf den Saal betreten. Es wird angezeigt, 
    // in welchem Saal, in welcher Reihe und auf welchem Sitzplatz der Gast Platz nehmen darf.
    record Erlaubt(Saal saal, Reihe reihe, Sitzplatz sitzplatz) implements Einlassentscheidung {}

    // Abgelehnt: Die Kinokarte konnte nicht entwertet werden, der Gast darf den Saal nicht betreten.
    // Es wird eine Liste von Ablehnungsgründen zurückgegeben, die dem Gast angezeigt
    record Abgelehnt(Ablehnungsgrund grund) implements Einlassentscheidung {}   // statt List<Ablehnungsgrund>
}