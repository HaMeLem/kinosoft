package de.wps.ddd.kino.wochenplanung.filmkatalog;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * Film als Aggregat des Filmkatalogs, identifiziert über den {@link Filmtitel}. Hält neben den
 * Stammdaten auch planungsinterne Details ({@link Verleih}), die kein anderer Kontext benötigt.
 */
@AggregateRoot
public record Film(
        @Identity Filmtitel titel,
        int laufzeit,
        String posterUrl,
        String beschreibung,
        String genre,
        String hauptdarsteller,
        String regie,
        String sprache,
        Altersfreigabe fsk,
        Verleih verleih) {
}
