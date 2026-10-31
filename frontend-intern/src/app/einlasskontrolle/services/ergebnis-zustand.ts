import { Injectable, signal } from '@angular/core';
import type { EinlassErgebnis } from '../ergebnis/ergebnis';

@Injectable({
  providedIn: 'root',
})
export class ErgebnisZustand {
  // Das letzte Scan-Ergebnis wird nur vorübergehend im Speicher
  // gehalten, damit die Ergebnisansicht darauf zugreifen kann.
  private readonly aktuellesErgebnis =
    signal<EinlassErgebnis | null>(null);

  readonly ergebnis = this.aktuellesErgebnis.asReadonly();

  setzeErgebnis(ergebnis: EinlassErgebnis): void {
    this.aktuellesErgebnis.set(ergebnis);
  }

  loescheErgebnis(): void {
    this.aktuellesErgebnis.set(null);
  }
}
