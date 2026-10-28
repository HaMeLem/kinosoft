package de.wps.ddd.kino.einlass.domain;

public interface KinokartenRepository {
//konkrete Implementierung fehlt noch, soll in infrstructure/persistence package erfolgen
    Kinokarte hole(KinokartenId id);
    void speichere(Kinokarte kinokarten);
}
