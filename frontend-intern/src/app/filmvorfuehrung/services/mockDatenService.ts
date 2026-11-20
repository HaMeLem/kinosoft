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
        geplanterBeginn: zeitIn(10).toISOString(),
        geplantesEnde: zeitIn(130).toISOString(),
        saal: 'Kleiner Saal',
        status: 'GEPLANT'
      },
      {
        filmvorfuehrungId: 2,
        filmtitel: 'Film B',
        geplanterBeginn: zeitIn(190).toISOString(),
        geplantesEnde: zeitIn(310).toISOString(),
        saal: 'Kleiner Saal',
        status: 'GEPLANT'
      },
      {
        filmvorfuehrungId: 3,
        filmtitel: 'Film C',
        geplanterBeginn: zeitIn(30).toISOString(),
        geplantesEnde: zeitIn(150).toISOString(),
        saal: 'Großer Saal',
        status: 'GEPLANT'
      },
      {
        filmvorfuehrungId: 4,
        filmtitel: 'Film D',
        geplanterBeginn: zeitIn(210).toISOString(),
        geplantesEnde: zeitIn(330).toISOString(),
        saal: 'Großer Saal',
        status: 'GEPLANT'
      }
    ];
  }
}
