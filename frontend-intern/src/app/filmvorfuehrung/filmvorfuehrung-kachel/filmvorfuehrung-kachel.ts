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
  },
})
export class FilmvorfuehrungKachel {
  readonly stundenBlockBreitePx = 160;

  @Input() filmvorfuehrung?: FilmvorfuehrungDto;

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
