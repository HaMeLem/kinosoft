import { Component, OnDestroy, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-saaluebersicht',
  imports: [DatePipe],
  templateUrl: './saaluebersicht.html',
  styleUrl: './saaluebersicht.css',
})
export class Saaluebersicht implements OnDestroy {

  // Vorläufige Saaldaten. Später werden die Säle aus dem Backend geladen.
  readonly saele = [
    { id: 1, name: 'Saal 1' },
    { id: 2, name: 'Saal 2' },
  ];

  readonly jetzt = signal(new Date());

  // Die Zeitachse beginnt drei Stunden vor der aktuellen Stunde.
  readonly startStunde =
    (this.jetzt().getHours() - 3 + 24) % 24;

  // Anzahl der angezeigten Stunden.
  readonly anzahlStunden = 6;

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
}
