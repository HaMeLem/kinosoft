package de.wps.ddd.kino.filmvorfuehrung.application;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.wps.ddd.kino.filmvorfuehrung.domain.FilmvorfuehrungRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsbeginn;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Filmvorfuehrung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;
import de.wps.ddd.kino.filmvorfuehrung.dtos.UnterbrechungResponseDto;
import de.wps.ddd.kino.filmvorfuehrung.dtos.UnterbrechungBeendenRequestDto;
import de.wps.ddd.kino.filmvorfuehrung.dtos.UnterbrechungBeginnenRequestDto;

@ExtendWith(MockitoExtension.class)
class FilmvorfuehrungServiceTest {

    @Mock 
    private FilmvorfuehrungRepository filmvorfuehrungRepository;

    @InjectMocks 
    private UnterbrechungService filmvorfuehrungService;

    private UnterbrechungBeginnenRequestDto unterbrechungBeginnenRequestDto;
    private UnterbrechungBeendenRequestDto unterbrechungBeendenRequestDto;
    private Filmvorfuehrung filmvorfuehrung;
    private Map<UnterbrechungId,Unterbrechung> unterbrechungen;
    private UnterbrechungId unterbrechungId;
    private UnterbrechungId falscheUnterbrechungId;
    private VorfuehrungId vorfuehrungId;
    private VorfuehrungId falscheVorfuehrungId;
    private Unterbrechungsbeginn unterbrechungsbeginn1;
    private Unterbrechungsende unterbrechungsende1;
    private Unterbrechung unterbrechung;

    @BeforeEach 
    void setUp() {
        unterbrechungId = UnterbrechungId.neu();
        falscheUnterbrechungId = UnterbrechungId.neu();
        vorfuehrungId = VorfuehrungId.neu();
        falscheVorfuehrungId = VorfuehrungId.neu();
        unterbrechungsbeginn1 = new Unterbrechungsbeginn(LocalDateTime.of(2026,11,22,17,20));
        unterbrechungsende1 = new Unterbrechungsende(LocalDateTime.of(2026,11,22,17,22));
    }
    /*
    @Test 
    void speichereUnterbrechungsbeginn(){
        // Arrange
        unterbrechungsbeginn1 = new Unterbrechungsbeginn(LocalDateTime.of(2026,11,22,17,20));
        unterbrechungBeginnenRequestDto = new UnterbrechungBeginnenRequestDto(
            vorfuehrungId,
            unterbrechungsbeginn1
        );
        //when(filmvorfuehrungRepository.hole(vorfuehrungId)).thenReturn(Optional.of(filmvorfuehrung));
        
        // Act
        UnterbrechungResponseDto responseDto = filmvorfuehrungService.unterbrechungsbeginnSpeichern(unterbrechungBeginnenRequestDto);
        
        // Assert
        ArgumentCaptor<Unterbrechung> captor = ArgumentCaptor.forClass(Unterbrechung.class);
        verify(unterbrechungen).put(unterbrechungId,captor.capture());
        Unterbrechung gespeicherteUnterbrechung = captor.getValue();
        assertThat(gespeicherteUnterbrechung.getUnterbrechungId()).isEqualTo(unterbrechungId);
        assertThat(gespeicherteUnterbrechung.getUnterbrechungsbeginn()).isEqualTo(unterbrechungsbeginn1);
        assertThat(responseDto.vorfuehrungId()).isEqualTo(vorfuehrungId);
        assertThat(responseDto.unterbrechungId()).isEqualTo(unterbrechungId);
    }

    @Test 
    void speichereUnterbrechungsende(){
        // Arrange
        
    }
    */
}
