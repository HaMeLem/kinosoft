import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Saal } from '../dtos/saal';
import { SaalService } from '../services/saal';
import { Header } from '../../shared/header/header';
import { LogoutDialog } from '../../shared/logout-dialog/logout-dialog';

@Component({
  selector: 'app-saaluebersicht',
  imports: [DatePipe, Header, LogoutDialog],
  templateUrl: './saaluebersicht.html',
  styleUrl: './saaluebersicht.css',
})
export class Saaluebersicht implements AfterViewInit, OnDestroy {

  @ViewChild('zeitplan', { static: true })
  private zeitplan?: ElementRef<HTMLElement>;

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

  // Der Zeitplan beginnt immer am Anfang des Tages.
  readonly startStunde = 0;

  // 25 Uhrzeiten bilden den gesamten Bereich von 00:00 bis 24:00.
  readonly anzahlStunden = 25;
  readonly stundenBlockBreitePx = 160;

  // Aktualisiert die Zeitbeschriftungen beim Stundenwechsel.
  readonly uhrzeiten = computed(() =>
    Array.from(
      { length: this.anzahlStunden },
      (_, index) =>
        `${String(index).padStart(2, '0')}:00`
    )
  );

  // Berechnet die Position der roten Jetzt-Linie.
  readonly jetztPositionPx = computed(() => {
    const aktuell = this.jetzt();

    const stundenSeitBeginn = aktuell.getHours() + aktuell.getMinutes() / 60;

    return 100 + stundenSeitBeginn * this.stundenBlockBreitePx;
  });

  // Berechnet die Endzeit des angezeigten Zeitraums.
  readonly endUhrzeit = computed(() => '24:00');

  ngAfterViewInit(): void {
    const element = this.zeitplan?.nativeElement;
    if (!element) {
      return;
    }

    const aktuell = this.jetzt();
    const stundeJetzt = aktuell.getHours() + aktuell.getMinutes() / 60;
    const startPosition = Math.max(0, stundeJetzt - 2.5) * this.stundenBlockBreitePx;

    element.scrollLeft = startPosition;
  }

  // Aktualisiert die aktuelle Uhrzeit alle 30 Sekunden.
  private readonly timer = setInterval(() => {
    this.jetzt.set(new Date());
  }, 30000);

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}
