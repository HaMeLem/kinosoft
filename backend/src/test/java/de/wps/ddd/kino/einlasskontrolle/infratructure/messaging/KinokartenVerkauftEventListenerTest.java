package de.wps.ddd.kino.einlasskontrolle.infratructure.messaging;

import de.wps.ddd.kino.einlasskontrolle.application.ImportiereKinokartenVerkauftService;
import de.wps.ddd.kino.einlasskontrolle.domain.Kinokarte;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenId;
import de.wps.ddd.kino.einlasskontrolle.domain.KinokartenRepository;
import de.wps.ddd.kino.einlasskontrolle.domain.Saal;
import de.wps.ddd.kino.einlasskontrolle.infrastructure.messaging.KinokartenVerkauftEventListener;
import de.wps.ddd.kino.kartenverkauf.KinokartenVerkauftDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;


class KinokartenVerkauftEventListenerTest {
    @Test
    void verarbeite_importiertDieVerkauftenKarten() {
        // given
        RecordingKinokartenRepository repository = new RecordingKinokartenRepository();
        ImportiereKinokartenVerkauftService importService =
                new ImportiereKinokartenVerkauftService(repository);
        KinokartenVerkauftEventListener listener =
                new KinokartenVerkauftEventListener(importService);

        UUID vorstellungId = UUID.randomUUID();
        UUID ersteKartenId = UUID.randomUUID();
        UUID zweiteKartenId = UUID.randomUUID();

        KinokartenVerkauftDto dto = new KinokartenVerkauftDto(
                UUID.randomUUID(),
                vorstellungId,
                "Film",
                LocalDateTime.of(2026, 10, 28, 20, 0),
                "kleiner Saal",
                List.of(
                        new KinokartenVerkauftDto.VerkaufteKarte(ersteKartenId, 3, 5),
                        new KinokartenVerkauftDto.VerkaufteKarte(zweiteKartenId, 3, 6)));

        // when
        listener.verarbeite(dto);

        // then
        assertThat(repository.gespeicherteKarten).hasSize(2);

        Kinokarte ersteKarte = repository.gespeicherteKarten.get(0);
        assertThat(ersteKarte.getId().id()).isEqualTo(ersteKartenId);
        assertThat(ersteKarte.getReihe().wert()).isEqualTo(3);
        assertThat(ersteKarte.getSitzplatz().wert()).isEqualTo(5);
        assertThat(ersteKarte.getSaal()).isEqualTo(Saal.KLEINER_SAAL);
        assertThat(ersteKarte.getFilmvorstellung().id()).isEqualTo(vorstellungId);

        Kinokarte zweiteKarte = repository.gespeicherteKarten.get(1);
        assertThat(zweiteKarte.getId().id()).isEqualTo(zweiteKartenId);
        assertThat(zweiteKarte.getReihe().wert()).isEqualTo(3);
        assertThat(zweiteKarte.getSitzplatz().wert()).isEqualTo(6);
    }

    private static class RecordingKinokartenRepository implements KinokartenRepository {
        private final List<Kinokarte> gespeicherteKarten = new ArrayList<>();

        // muss implementiert sein, brauche ich aber gar nicht
        @Override
        public Kinokarte hole(KinokartenId id) {
            throw new UnsupportedOperationException("hole() wird in diesem Test nicht benötigt");
        }

        @Override
        public void speichere(Kinokarte kinokarte) {
            gespeicherteKarten.add(kinokarte);
        }
    }
}
