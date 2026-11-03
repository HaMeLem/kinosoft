package de.wps.ddd.kino.einlasskontrolle.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;


/**
 * Stellt die Uhr (Clock) als Spring-Bean bereit.
 * Der EinlasskontrolleService bekommt sie per Konstruktor injiziert
 * und nutzt sie, um die aktuelle Zeit zu ermitteln.
 */
// Markiert die Klasse als Konfiguration. Spring liest sie beim Start ein
// und führt die darin enthaltenen @Bean-Methoden aus.

@Configuration
public class EinlassClock {

    // Echte Systemzeit, aber fest auf die Zeitzone Berlin eingestellt.
    // So ist der Vergleich mit den geplanten Startzeiten unabhängig davon,
    // in welcher Zeitzone der Server oder Container läuft.
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Berlin"));
    }
}