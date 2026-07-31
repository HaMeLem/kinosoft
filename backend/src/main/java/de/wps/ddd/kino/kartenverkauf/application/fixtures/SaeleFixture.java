package de.wps.ddd.kino.kartenverkauf.application.fixtures;

import de.wps.ddd.kino.common.fixtures.Fixture;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saal;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalname;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Saele;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Stellt die (Demo-)Säle des Kartenverkaufs bereit. Läuft dank {@link Order} vor der
 * {@code WochenplanFixture}, damit die Säle schon vorhanden sind, wenn die (async) Übernahme des
 * Wochenplans die Bestuhlung nachschlägt.
 */
@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class SaeleFixture implements Fixture {

    private final Saele saele;

    @Override
    public void install() {
        log.info("Erzeuge Säle...");
        saele.speichere(new Saal(new Saalname("kleiner Saal"), 4, 8));
        saele.speichere(new Saal(new Saalname("großer Saal"), 6, 12));
    }
}
