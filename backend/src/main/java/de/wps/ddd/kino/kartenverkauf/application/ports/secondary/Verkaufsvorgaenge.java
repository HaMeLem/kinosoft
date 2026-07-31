package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Verkaufsvorgang;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.ZahlungsvorgangId;
import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface Verkaufsvorgaenge {

    void speichere(Verkaufsvorgang verkaufsvorgang);

    Verkaufsvorgang hole(Auftragsnummer auftragsnummer);

    Verkaufsvorgang holeZuZahlungsvorgang(ZahlungsvorgangId zahlungsvorgangId);
}
