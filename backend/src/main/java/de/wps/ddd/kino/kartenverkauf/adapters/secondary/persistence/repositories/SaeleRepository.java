package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.repositories;

import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalname;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Saele;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory-Repository der Säle des Kartenverkaufs (wie zahlung). Befüllt wird es über die
 * {@code SaeleFixture}.
 */
@Component
public class SaeleRepository implements Saele {

    private final ConcurrentHashMap<Saalname, Saal> saele = new ConcurrentHashMap<>();

    @Override
    public Optional<Saal> finde(Saalname name) {
        return Optional.ofNullable(saele.get(name));
    }

    @Override
    public void speichere(Saal saal) {
        saele.put(saal.name(), saal);
    }
}
