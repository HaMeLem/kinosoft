package de.wps.ddd.kino.einlasskontrolle.domain;

// Sammlung der möglichen Ablehnungsgründe für den Einlass in den Kinosaal. Jeder Ablehnungsgrund hat eine zugehörige Meldung, die dem Benutzer angezeigt werden kann.
public enum Ablehnungsgrund {
    UNGUELTIGER_CODE("Einlass nicht erlaubt: ungültiger Code"),
    FALSCHE_VORSTELLUNG("Einlass nicht erlaubt: falsche Vorstellung"),
    ZU_FRUEH("Einlass nicht erlaubt: zu früh"),
    ZU_SPAET("Einlass nicht erlaubt: zu spät"),
    BEREITS_ENTWERTET("Einlass nicht erlaubt: Ticket bereits entwertet");


    private final String meldung;

    // Konstruktor für den Ablehnungsgrund, der die zugehörige Meldung setzt.
    // wird verwendet , um in der Einlassentscheidung die Meldung anzuzeigen, die dem Benutzer angezeigt werden soll. 
    Ablehnungsgrund(String meldung) {
        this.meldung = meldung;
    }
    // Gibt die Meldung zurück, die dem Benutzer angezeigt werden soll, wenn der Einlass abgelehnt wird.
    public String meldung() {
        return meldung;
    }
}