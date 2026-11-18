import { Component, OnDestroy, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Saal, SaalService } from '../services/saal';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-saaluebersicht',
  imports: [DatePipe, RouterLink],
  templateUrl: './saaluebersicht.html',
  styleUrl: './saaluebersicht.css',
})
export class Saaluebersicht implements OnDestroy {

  // Steuert die Anzeige des Ausloggen-Dialogs.
  readonly abmeldungOffen = signal(false);

  abmeldungAnzeigen(): void {
    this.abmeldungOffen.set(true);
  }

  abmeldungSchliessen(): void {
    this.abmeldungOffen.set(false);
  }

  readonly menueOffen = signal(false);

  // Lädt die vorläufigen Saaldaten über den Service.
  readonly saele = toSignal(
    inject(SaalService).holeSaele(),
    { initialValue: [] as Saal[] }
  );

  readonly jetzt = signal(new Date());

  // Die Zeitachse beginnt drei Stunden vor der aktuellen Stunde.
  readonly startStunde =
    (this.jetzt().getHours() - 3 + 24) % 24;

  // Anzahl der angezeigten Stunden.
  readonly anzahlStunden = 9;

  readonly uhrzeiten = Array.from(
    { length: this.anzahlStunden },
    (_, index) =>
      `${String((this.startStunde + index) % 24).padStart(2, '0')}:00`
  );

  // Berechnet die Position der roten Jetzt-Linie.
  readonly jetztPositionPx = computed(() => {
    const aktuell = this.jetzt();

    const stundenSeitBeginn =
      (aktuell.getHours() - this.startStunde + 24) % 24;

    return 120 + (stundenSeitBeginn + aktuell.getMinutes() / 60) * 140;
  });

  // Aktualisiert die aktuelle Uhrzeit alle 30 Sekunden.
  private readonly timer = setInterval(() => {
    this.jetzt.set(new Date());
  }, 30000);

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }

  // Berechnet die Endzeit des angezeigten Zeitraums.
  readonly endUhrzeit =
    `${String((this.startStunde + this.anzahlStunden) % 24).padStart(2, '0')}:00`;
}
