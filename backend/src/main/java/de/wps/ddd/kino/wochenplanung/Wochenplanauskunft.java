package de.wps.ddd.kino.wochenplanung;

/**
 * Exponierte Abfrage-API der Wochenplanung. Nach dem {@link WochenplanErstelltDto}-Ereignis holen
 * sich Filmauswahl und Kartenverkauf über diese Schnittstelle den {@link WochenplanDto} und bilden
 * ihn auf ihre eigenen Strukturen ab (Benachrichtigungs-Event + Rückfrage).
 */
public interface Wochenplanauskunft {

    WochenplanDto holeWochenplan(int jahr, int kalenderwoche);
}
