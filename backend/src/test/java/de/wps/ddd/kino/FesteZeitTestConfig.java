package de.wps.ddd.kino;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Pinnt die Anwendungsuhr in Integrationstests auf den festen Bezugstag <b>2025-03-19</b> (Mi, KW 12/2025).
 * Dadurch reproduziert die {@code WochenplanFixture} exakt denselben Fahrplan wie zur Entwicklungszeit,
 * sodass die Datums-Literale der Integrationstests ({@code of(2025, 3, ...)}) stabil bleiben.
 *
 * @see IntegrationTestMitWochenplan
 */
@TestConfiguration(proxyBeanMethods = false)
public class FesteZeitTestConfig {

    private static final LocalDate BEZUGSTAG = LocalDate.of(2025, 3, 19);

    @Bean
    @Primary
    Clock festeUhr() {
        return Clock.fixed(BEZUGSTAG.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }
}
