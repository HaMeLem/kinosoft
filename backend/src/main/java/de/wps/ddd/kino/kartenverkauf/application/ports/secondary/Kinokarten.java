package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Auftragsnummer;
import de.wps.ddd.kino.kartenverkauf.application.domain.kartenverkauf.Kinokarte;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;

@Repository
public interface Kinokarten {

    void speichere(List<Kinokarte> kinokarten);

    List<Kinokarte> finde(Auftragsnummer auftragsnummer);
}
