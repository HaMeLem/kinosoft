// Mock Daten für zB Filme, Filmvorführungen

/*
Filmvorführung würde aus dem Backend folgendes enthalten:
filmtitel
geplanterBeginn
geplantesEnde
saal
status
filmvorfuehrungId
 */

import { Injectable } from '@angular/core';
import { FilmvorfuehrungDto } from '../dtos/filmvorfuehrungDto';

@Injectable({
  providedIn: 'root'
})
export class MockDatenService {

  /**
   * Liefert Testdaten für Filmvorführungen.
   * Wird später durch die Backend-Anbindung ersetzt.
   */
  getFilmvorfuehrungen(): FilmvorfuehrungDto[] {

    const jetzt = new Date();

    // Berechnet eine Uhrzeit relativ zum aktuellen Zeitpunkt.
    const zeitIn = (minuten: number): Date =>
      new Date(jetzt.getTime() + minuten * 60_000);


    return [
      {
        filmvorfuehrungId: 1,
        filmtitel: 'Film A',
        geplanterBeginn: zeitIn(-10).toISOString(),
        geplantesEnde: zeitIn(110).toISOString(),
        saal: 'Kleiner Saal',
        status: 'GEPLANT'
      },
      {
        filmvorfuehrungId: 2,
        filmtitel: 'Film B',
        geplanterBeginn: zeitIn(170).toISOString(),
        geplantesEnde: zeitIn(290).toISOString(),
        saal: 'Kleiner Saal',
        status: 'GEPLANT'
      },
      {
        filmvorfuehrungId: 3,
        filmtitel: 'Film C',
        geplanterBeginn: zeitIn(1).toISOString(),
        geplantesEnde: zeitIn(121).toISOString(),
        saal: 'Großer Saal',
        status: 'GEPLANT'
      },
      {
        filmvorfuehrungId: 4,
        filmtitel: 'Film D',
        geplanterBeginn: zeitIn(181).toISOString(),
        geplantesEnde: zeitIn(301).toISOString(),
        saal: 'Großer Saal',
        status: 'GEPLANT'
      }
    ];
  }
}
