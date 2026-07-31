package de.wps.ddd.kino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Clock;

@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Zeitquelle der Anwendung. In Produktion die Systemuhr, sodass die Demodaten (siehe
     * {@code WochenplanFixture}) stets die aktuelle Kalenderwoche abbilden. Tests überschreiben
     * diesen Bean mit einer festen Uhr (Bezugstag 2025-03-19), um deterministische Datumswerte zu erhalten.
     */
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
