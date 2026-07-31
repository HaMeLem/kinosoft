package de.wps.ddd.kino.wochenplanung.wochenplan;

import de.wps.ddd.kino.wochenplanung.filmkatalog.Film;
import de.wps.ddd.kino.wochenplanung.filmkatalog.Filmtitel;
import de.wps.ddd.kino.wochenplanung.saalverwaltung.Saalname;
import org.jmolecules.ddd.annotation.Association;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eine im Wochenplan eingeplante Vorstellung — eine Entität mit eigener Identität
 * ({@link VorstellungId}) innerhalb des {@link Wochenplan}-Aggregats. Referenziert Film und Saal nur
 * über ihre Identitäten ({@link Filmtitel} / {@link Saalname}), nicht über die Aggregate selbst.
 */
@Entity
public record Vorstellung(
        @Identity
        VorstellungId id,
        @Association(aggregateType = Film.class)
        Filmtitel film,
        Saalname saal,
        Vorstellungskategorie kategorie,
        Vorstellungszeiten zeiten) {

    public static Vorstellung plane(Film film, Saalname saal, Vorstellungskategorie kategorie, LocalDateTime vorstellungsbeginn) {
        return new Vorstellung(
                new VorstellungId(UUID.randomUUID()),
                film.titel(),
                saal,
                kategorie,
                Vorstellungszeiten.plane(vorstellungsbeginn, film.laufzeit()));
    }
}
