import { DatePipe } from '@angular/common';
import { Component, OnDestroy, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Saal } from '../dtos/saal';
import { SaalService } from '../services/saal';

@Component({
  selector: 'app-saaluebersicht',
  imports: [DatePipe, RouterLink],
  templateUrl: './saaluebersicht.html',
  styleUrl: './saaluebersicht.css',
})
export class Saaluebersicht implements OnDestroy {

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

  // Aktuelle Uhrzeit.
  readonly jetzt = signal(new Date());

  // Passt die Startstunde automatisch an die aktuelle Uhrzeit an.
  readonly startStunde = computed(() =>
    (this.jetzt().getHours() - 3 + 24) % 24
  );

  // Anzahl der angezeigten Stunden.
  readonly anzahlStunden = 9;

  // Aktualisiert die Zeitbeschriftungen beim Stundenwechsel.
  readonly uhrzeiten = computed(() =>
    Array.from(
      { length: this.anzahlStunden },
      (_, index) =>
        `${String((this.startStunde() + index) % 24).padStart(2, '0')}:00`
    )
  );

  // Berechnet die Position der roten Jetzt-Linie.
  readonly jetztPositionPx = computed(() => {
    const aktuell = this.jetzt();

    const stundenSeitBeginn =
      (aktuell.getHours() - this.startStunde() + 24) % 24;

    return 100 + (stundenSeitBeginn + aktuell.getMinutes() / 60) * 140;
  });

  // Berechnet die Endzeit des angezeigten Zeitraums.
  readonly endUhrzeit = computed(() =>
    `${String((this.startStunde() + this.anzahlStunden) % 24).padStart(2, '0')}:00`
  );

  // Aktualisiert die aktuelle Uhrzeit alle 30 Sekunden.
  private readonly timer = setInterval(() => {
    this.jetzt.set(new Date());
  }, 30000);

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}
