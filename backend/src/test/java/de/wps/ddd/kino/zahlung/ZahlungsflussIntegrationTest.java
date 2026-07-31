package de.wps.ddd.kino.zahlung;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.zahlung.application.Zahlungsabwicklung;
import de.wps.ddd.kino.zahlung.domain.Zahlungen;
import de.wps.ddd.kino.zahlung.domain.Zahlungsreferenz;
import de.wps.ddd.kino.zahlung.domain.Zahlungsstatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ZahlungsflussIntegrationTest {

    @Autowired
    private Zahlungsabwicklung zahlungsabwicklung;

    @Autowired
    private Zahlungen zahlungen;

    @Test
    void starteUndBezahle_setztAsynchronEndstatus(Scenario scenario) {
        var referenz = new Zahlungsreferenz(UUID.randomUUID());

        scenario.publish(new StarteZahlungDto(referenz.wert(), 5000))
                .andWaitAtMost(Duration.ofSeconds(2))
                .andWaitForStateChange(() -> status(referenz))
                .andVerify(status -> assertThat(status.get()).isEqualTo(Zahlungsstatus.Offen));

        scenario.stimulate(() -> zahlungsabwicklung.bezahle(referenz))
                .andWaitAtMost(Duration.ofSeconds(10))
                .andWaitForStateChange(() -> status(referenz), s -> s.get() != Zahlungsstatus.Offen)
                .andVerify(status -> assertThat(status.get()).isIn(Zahlungsstatus.Eingegangen, Zahlungsstatus.Abgebrochen));
    }

    private Optional<Zahlungsstatus> status(Zahlungsreferenz referenz) {
        try {
            return Optional.of(zahlungen.hole(referenz).getStatus());
        } catch (RessourceNichtGefunden nochNichtRegistriert) {
            return Optional.empty();
        }
    }
}
