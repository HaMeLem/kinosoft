package de.wps.ddd.kino.kartenverkauf.application.ports.secondary;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.VorstellungId;
import org.jmolecules.ddd.annotation.Repository;

@Repository
public interface SaalplanStapel {

    Saalplan holeSaalplan(VorstellungId vorstellungId);

    boolean existiertSaalplan(VorstellungId vorstellungId);

    void legeZurueck(Saalplan saalplan);
}
