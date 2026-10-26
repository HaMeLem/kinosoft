package de.wps.ddd.kino.einlass.domain;

public interface KinokartenRepository {

    Kinokarte hole(KinokartenId id);
    void speichere(Kinokarte kinokarten);
}
