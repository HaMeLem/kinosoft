package de.wps.ddd.kino.wochenplanung.persistence;

import de.wps.ddd.kino.wochenplanung.wochenplan.Kalenderwoche;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplan;
import de.wps.ddd.kino.wochenplanung.wochenplan.Wochenplaene;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WochenplanRepository implements Wochenplaene {

    private final ConcurrentHashMap<Kalenderwoche, Wochenplan> wochenplaene = new ConcurrentHashMap<>();

    @Override
    public Optional<Wochenplan> finde(Kalenderwoche kalenderwoche) {
        return Optional.ofNullable(wochenplaene.get(kalenderwoche));
    }

    @Override
    public void speichere(Wochenplan wochenplan) {
        wochenplaene.put(wochenplan.getKalenderwoche(), wochenplan);
    }
}
