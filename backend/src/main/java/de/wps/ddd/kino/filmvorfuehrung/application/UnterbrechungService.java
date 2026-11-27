package de.wps.ddd.kino.filmvorfuehrung.application;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.wps.ddd.kino.common.architecture.ApplicationService;
import de.wps.ddd.kino.filmvorfuehrung.domain.FilmvorfuehrungRepository;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechung;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.UnterbrechungId;
import de.wps.ddd.kino.filmvorfuehrung.domain.unterbrechung.Unterbrechungsende;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.Filmvorfuehrung;
import de.wps.ddd.kino.filmvorfuehrung.domain.vorfuehrung.VorfuehrungId;
import de.wps.ddd.kino.filmvorfuehrung.dtos.UnterbrechungResponseDto;
import de.wps.ddd.kino.filmvorfuehrung.dtos.UnterbrechungBeendenRequestDto;
import de.wps.ddd.kino.filmvorfuehrung.dtos.UnterbrechungBeginnenRequestDto;

@ApplicationService 
@Service
public class UnterbrechungService {

     private final FilmvorfuehrungRepository filmvorfuehrungRepository;

     public UnterbrechungService(FilmvorfuehrungRepository filmvorfuehrungRepository) {
          this.filmvorfuehrungRepository = filmvorfuehrungRepository;
     }

     public UnterbrechungResponseDto unterbrechungsbeginnSpeichern(UnterbrechungBeginnenRequestDto requestDto) {
          
          VorfuehrungId vorfuehrungId = requestDto.vorfuehrungId();
          
          // Holt die Filmvorfuehrung mit der Id des Dto
          Filmvorfuehrung filmvorfuehrung = filmvorfuehrungRepository.hole(vorfuehrungId)
               .orElseThrow(() -> new RuntimeException("Vorführung mit der ID " + vorfuehrungId + " nicht gefunden"));

          // Neue Unterbrechung mit Id und Beginn erstellen
          UnterbrechungId unterbrechungId = UnterbrechungId.neu();
          Unterbrechung unterbrechung = new Unterbrechung(unterbrechungId, requestDto.unterbrechungsbeginn());

          // Die neue Unterbrechung speichern
          Map<UnterbrechungId,Unterbrechung> unterbrechungen = filmvorfuehrung.getUnterbrechungen();
          unterbrechungen.put(unterbrechungId, unterbrechung);
          
          // Dto mit der Id der Filmvorführung und der neuvergebenen Id der angelegten Unterbrechung zurückgeben
          UnterbrechungResponseDto responseDto = new UnterbrechungResponseDto(vorfuehrungId, unterbrechungId);
          return responseDto;
     }

     public UnterbrechungResponseDto unterbrechungsendeSpeichern(UnterbrechungBeendenRequestDto requestDto) {
          
          VorfuehrungId vorfuehrungId = requestDto.vorfuehrungId();
          UnterbrechungId unterbrechungId = requestDto.unterbrechungId();
          Unterbrechungsende unterbrechungsende = requestDto.unterbrechungsende();

          // Holt die Filmvorfuehrung mit der Id des Dto
          Filmvorfuehrung filmvorfuehrung = filmvorfuehrungRepository.hole(vorfuehrungId)
               .orElseThrow(() -> new RuntimeException("Vorführung mit der ID " + vorfuehrungId + " nicht gefunden"));

          // Holt die Unterbrechung mit der Id des Dto aus der Map und speichert das Unterbrechungsende
          Map<UnterbrechungId,Unterbrechung> unterbrechungen = filmvorfuehrung.getUnterbrechungen();
          if(unterbrechungen.isEmpty()) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Die Filmvorführunge mit der ID " + vorfuehrungId + " hat keine Unterbrechungen");
          }
          Unterbrechung unterbrechung = unterbrechungen.get(unterbrechungId);
          unterbrechung.unterbrechungsendeSpeichern(unterbrechungsende);
          unterbrechungen.put(unterbrechungId, unterbrechung);
          
          // Dto mit der Id der Filmvorführung und der neuvergebenen Id der angelegten Unterbrechung zurückgeben
          UnterbrechungResponseDto responseDto = new UnterbrechungResponseDto(vorfuehrungId, unterbrechungId);
          return responseDto;
     }

     public Map<UnterbrechungId,Unterbrechung> holeAlleUnterbrechungenEinerFilmvorfuehrung(VorfuehrungId vorfuehrungId) {
          
          Filmvorfuehrung filmvorfuehrung = filmvorfuehrungRepository.hole(vorfuehrungId)
               .orElseThrow(() -> new RuntimeException("Vorführung mit der ID " + vorfuehrungId + " nicht gefunden"));

          Map<UnterbrechungId,Unterbrechung> unterbrechungen = filmvorfuehrung.getUnterbrechungen();
          if(unterbrechungen.isEmpty()) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Die Filmvorführunge mit der ID " + vorfuehrungId + " hat keine Unterbrechungen");
          }
          return unterbrechungen;
     }

     public Unterbrechung holeUnterbrechungMitUnterbrechungsId(VorfuehrungId vorfuehrungId, UnterbrechungId unterbrechungId) {
          
          Filmvorfuehrung filmvorfuehrung = filmvorfuehrungRepository.hole(vorfuehrungId)
               .orElseThrow(() -> new RuntimeException("Vorführung mit der ID " + vorfuehrungId + " nicht gefunden"));

          Map<UnterbrechungId,Unterbrechung> unterbrechungen = filmvorfuehrung.getUnterbrechungen();
          if(unterbrechungen.isEmpty()) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unterbrechung mit der ID " + unterbrechungId + " nicht gefunden");
          }
          Unterbrechung unterbrechung = unterbrechungen.get(unterbrechungId);
          return unterbrechung;
     } 
}