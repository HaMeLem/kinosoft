package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalname;
import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

/**
 * Repository-Port für die Säle des Kartenverkaufs (Stammdaten: Bestuhlung je Saal). Die Wochenplanung
 * liefert nur den Saalnamen; die Bestuhlung ist kartenverkauf-eigen. Die Demo-Säle werden über eine
 * Fixture bereitgestellt.
 */
@Repository
public interface Saele {

    Optional<Saal> finde(Saalname name);

    void speichere(Saal saal);
}
