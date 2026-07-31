package de.wps.ddd.kino.kartenverkauf.application.services;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalname;
import de.wps.ddd.kino.kartenverkauf.application.domain.sitzplatzvergabe.Saalplan;
import de.wps.ddd.kino.kartenverkauf.application.domain.vorstellungen.WochenplanUebernommen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.AktuelleVorstellungen;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.SaalplanStapel;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Saele;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Wochenplaene;
import de.wps.ddd.kino.kartenverkauf.application.ports.secondary.Wochenplanmeldungen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Übernimmt nach dem {@code WochenplanErstelltDto} den Wochenplan der Wochenplanung in den
 * Kartenverkauf: bezieht die Vorstellungen — bereits in die eigene Domäne übersetzt — über den
 * {@link Wochenplaene}-Sekundärport, persistiert sie und legt zu jeder aus der {@link Saele
 * kartenverkauf-eigenen Bestuhlung} ihres Saals einen (zunächst leeren) Saalplan an. Anschließend
 * meldet er das {@link WochenplanUebernommen}, worauf die {@code SaalplanBelegungFixture} die
 * Demo-Belegung ergänzt.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ImportiereWochenplan implements de.wps.ddd.kino.kartenverkauf.application.ports.primary.ImportiereWochenplan {

    private final Wochenplaene wochenplaene;
    private final Saele saele;
    private final AktuelleVorstellungen aktuelleVorstellungen;
    private final SaalplanStapel saalplanStapel;
    private final Wochenplanmeldungen wochenplanmeldungen;

    @Override
    public void fuer(int jahr, int kalenderwoche) {
        var vorstellungen = wochenplaene.holeWochenplan(jahr, kalenderwoche);
        log.info("Kartenverkauf importiert Wochenplan KW {}/{}: {} Vorstellungen", kalenderwoche, jahr, vorstellungen.size());

        for (var vorstellung : vorstellungen) {
            aktuelleVorstellungen.speichere(vorstellung);
            if (!saalplanStapel.existiertSaalplan(vorstellung.getId())) {
                var saal = saele.finde(new Saalname(vorstellung.getSaal().name())).orElse(null);
                RessourceNichtGefunden.wenn(saal == null, "Saal " + vorstellung.getSaal().name() + " nicht bekannt");
                saalplanStapel.legeZurueck(Saalplan.leer(vorstellung.getId(), saal.reihen(), saal.plaetzeProReihe()));
            }
        }

        wochenplanmeldungen.melde(new WochenplanUebernommen(jahr, kalenderwoche));
    }
}
