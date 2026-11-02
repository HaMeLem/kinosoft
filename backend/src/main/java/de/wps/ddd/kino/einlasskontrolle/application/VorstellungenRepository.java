package de.wps.ddd.kino.einlasskontrolle.application;

import de.wps.ddd.kino.filmauswahl.data.Vorstellung;
import java.util.UUID;

public interface VorstellungenRepository {

    Vorstellung hole(UUID uuid);

}