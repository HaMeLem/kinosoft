import { DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FilmvorfuehrungDto } from '../dtos/filmvorfuehrungDto';

@Component({
  selector: 'app-filmvorfuehrung-kachel',
  imports: [DatePipe],
  templateUrl: './filmvorfuehrung-kachel.html',
  styleUrl: './filmvorfuehrung-kachel.css',
  host: {
    class: 'filmvorfuehrung-kachel',
    '[style.left.px]': 'positionPx',
    '[style.width.px]': 'breitePx',
    '[class.baldiger-start]': 'baldigerStart',
    '[class.ueberfaellig]': 'ueberfaellig',
  },
})
export class FilmvorfuehrungKachel {
  readonly stundenBlockBreitePx = 160;

  @Input() filmvorfuehrung?: FilmvorfuehrungDto;

  @Input() aktuelleZeit?: Date;

  // Außerdem unterstützen, falls die Kachel an anderen Stellen
  // einfach mit dem allgemeineren Namen "vorfuehrung" verwendet wird.
  @Input() set vorfuehrung(vorfuehrung: FilmvorfuehrungDto | undefined) {
    this.filmvorfuehrung = vorfuehrung;
  }

  get statusAnzeige(): string {
    if (!this.filmvorfuehrung) {
      return '';
    }

    const statusLabels: Record<string, string> = {
      GEPLANT: 'Geplant',
    };

    return statusLabels[this.filmvorfuehrung.status] ?? this.filmvorfuehrung.status;
  }

  /**
   * Berechnet verbleibende Zeit bis zu Filmvorführung Beginn in ms.
   * Negativer Wert bedeutet, Beginn ist überfällig.
   */
  get verbleibendeZeitMs(): number {
    // falls Filmvorführung undefined ist
    if (!this.filmvorfuehrung || !this.aktuelleZeit) {
      return 0;
    }

    // Uhrzeit-Differenz berechnen
    const beginn = new Date(this.filmvorfuehrung.geplanterBeginn).getTime();
    return beginn - this.aktuelleZeit.getTime();
  }

  /**
   * Formatiert die verbleibende Zeit bis zum geplanten Beginn
   * als Countdown im Format MM:SS.
   */
  get countdown(): string {
    const differenzMs = this.verbleibendeZeitMs;

    const sekundenGesamt = differenzMs > 0 ? Math.ceil(differenzMs/1000) : Math.floor(Math.abs(differenzMs) / 1000);

    const minuten = Math.floor(sekundenGesamt / 60);
    const sekunden = sekundenGesamt % 60;

    return `${String(minuten).padStart(2, '0')}:${String(sekunden).padStart(2, '0')}`;
  }

  /**
   * Prüft, ob Filmvorführung innerhalb der nächsten
   * 15 Minuten beginnt und noch geplant ist.
   */
  get baldigerStart(): boolean {
    return this.filmvorfuehrung?.status === 'GEPLANT'
      && this.verbleibendeZeitMs > 0
      && this.verbleibendeZeitMs <= 15 * 60_000;
  }

  /**
   * Prüft, ob geplanter Beginn überschritten wurde,
   * aber Filmvorführung noch Status "geplant" hat.
   */
  get ueberfaellig(): boolean {
    return this.filmvorfuehrung?.status === 'GEPLANT'
      && this.verbleibendeZeitMs <= 0;
  }


  get positionPx(): number {
    if (!this.filmvorfuehrung) {
      return 0;
    }

    const beginn = new Date(this.filmvorfuehrung.geplanterBeginn);
    const stunde = beginn.getHours() + beginn.getMinutes() / 60;
    return stunde * this.stundenBlockBreitePx;
  }

  get breitePx(): number {
    if (!this.filmvorfuehrung) {
      return 0;
    }

    const beginn = new Date(this.filmvorfuehrung.geplanterBeginn);
    const ende = new Date(this.filmvorfuehrung.geplantesEnde);
    const dauerMs = Math.max(0, ende.getTime() - beginn.getTime());
    return dauerMs / 3_600_000 * this.stundenBlockBreitePx;
  }

  positionFuerVorfuehrung(vorfuehrung: FilmvorfuehrungDto): number {
    const beginn = new Date(vorfuehrung.geplanterBeginn);
    const stunde = beginn.getHours() + beginn.getMinutes() / 60;
    return stunde * this.stundenBlockBreitePx;
  }

  breiteFuerVorfuehrung(vorfuehrung: FilmvorfuehrungDto): number {
    const beginn = new Date(vorfuehrung.geplanterBeginn);
    const ende = new Date(vorfuehrung.geplantesEnde);
    const dauerMs = Math.max(0, ende.getTime() - beginn.getTime());
    return dauerMs / 3_600_000 * this.stundenBlockBreitePx;
  }
}


/*
 Komponente, die die Kacheln mit den Filmvorführungen präsentiert
 Zeigt an: Filmtitel, GeplanterBeginn - GeplantesEnde, Status (bzw Countdown) -> verändert Farbe der Kachel
 */
