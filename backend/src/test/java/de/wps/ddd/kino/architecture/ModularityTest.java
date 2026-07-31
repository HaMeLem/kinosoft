package de.wps.ddd.kino.architecture;

import de.wps.ddd.kino.Application;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Prüft die Spring-Modulith-Modulstruktur: Module dürfen nur über exponierte Typen (öffentliche
 * Modul-API, benannte Schnittstellen oder Events) voneinander abhängen und müssen zyklenfrei sein.
 */
class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void erfuelltModulithRegeln() {
        modules.verify();
    }
}
