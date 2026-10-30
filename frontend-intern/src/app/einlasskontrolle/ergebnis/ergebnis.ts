import { Component, input, signal } from '@angular/core';

export type EinlassErgebnis =
  | {
  typ: 'erfolg';
  saal: number;
  reihe: number;
  sitzplatz: number;
}
  | {
  typ: 'fehler';
  titel: string;
  beschreibung?: string;
};

@Component({
  selector: 'app-ergebnis',
  imports: [],
  templateUrl: './ergebnis.html',
  styleUrl: './ergebnis.css',
})
export class Ergebnis {
  readonly ergebnis = input<EinlassErgebnis | null>(null);

  readonly abmeldungOffen = signal(false);

  abmeldungAnzeigen(): void {
    this.abmeldungOffen.set(true);
  }

  abmeldungSchliessen(): void {
    this.abmeldungOffen.set(false);
  }
}
