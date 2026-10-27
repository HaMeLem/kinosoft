package de.wps.ddd.kino.einlasskontrolle.domain;

public interface KinokartenRepository {

    Kinokarte hole(KinokartenId id);
    void speichere(Kinokarte kinokarten);
}
