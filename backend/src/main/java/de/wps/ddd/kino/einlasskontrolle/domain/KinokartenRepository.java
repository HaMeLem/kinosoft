package de.wps.ddd.kino.einlasskontrolle.domain;

import java.util.Optional;

public interface KinokartenRepository {

    Optional<Kinokarte> hole(KinokartenId id);

    void speichere(Kinokarte kinokarte);
}

