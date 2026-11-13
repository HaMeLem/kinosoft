import {
  AfterViewInit,
  Component,
  DestroyRef,
  ElementRef,
  OnDestroy,
  ViewChild,
  inject,
  signal
} from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BrowserQRCodeReader, IScannerControls } from '@zxing/browser';

import { Einlass } from '../services/einlass';
import { ErgebnisZustand } from '../services/ergebnis-zustand';

interface EinlassAblehnung {
  grund: string;
  meldung: string;
}

@Component({
  selector: 'app-scanner',
  imports: [],
  templateUrl: './scanner.html',
  styleUrl: './scanner.css',
})
export class Scanner implements AfterViewInit, OnDestroy {
  @ViewChild('cameraVideo')
  private cameraVideo!: ElementRef<HTMLVideoElement>;

  private readonly einlass = inject(Einlass);
  private readonly ergebnisZustand = inject(ErgebnisZustand);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly abmeldungOffen = signal(false);
  readonly kameraLaeuft = signal(false);
  readonly kameraFehler = signal('');
  readonly pruefungLaeuft = signal(false);

  private readonly qrReader = new BrowserQRCodeReader();
  private scanControls?: IScannerControls;
  private kameraStartet = false;
  private scanAbgeschlossen = false;
  private zerstoert = false;

  ngAfterViewInit(): void {
    // Startet die Kamera, sobald das Videoelement verfügbar ist.
    void this.kameraStarten();
  }

  async kameraStarten(): Promise<void> {
    if (
      this.kameraStartet ||
      this.kameraLaeuft() ||
      this.pruefungLaeuft() ||
      this.abmeldungOffen() ||
      this.zerstoert
    ) {
      return;
    }
    this.kameraStartet = true;
    this.scanAbgeschlossen = false;
    this.kameraFehler.set('');
    this.ergebnisZustand.loescheErgebnis();

    try {
      const controls = await this.qrReader.decodeFromVideoDevice(
        undefined,
        this.cameraVideo.nativeElement,
        (result, _error, controls) => {
          if (!result || this.zerstoert || this.scanAbgeschlossen) {
            return;
          }

          const qrInhalt = result.getText().trim();

          const uuidFormat =
            /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

          // Ungültige QR-Codes ignorieren, damit weitere Tickets
          // ohne erneuten Kamerastart gescannt werden können.
          if (!uuidFormat.test(qrInhalt)) {
            this.kameraFehler.set(
              'Ungültiger QR-Code. Bitte scanne eine Kinokarte.'
            );
            return;
          }

          this.kameraFehler.set('');
          this.scanAbgeschlossen = true;
          controls.stop();
          this.kameraLaeuft.set(false);
          this.scanControls = undefined;
          this.kinokartePruefen(qrInhalt);
        }
      );

      // Die Kamerafreigabe kann verzögert erfolgen.
      // Falls die Seite inzwischen verlassen oder der Scan bereits
      // abgeschlossen wurde, wird die Kamera sofort gestoppt.
      if (
        this.zerstoert ||
        this.abmeldungOffen() ||
        this.scanAbgeschlossen
      ) {
        controls.stop();
      } else {
        this.scanControls = controls;
        this.kameraLaeuft.set(true);
      }
    } catch {
      if (!this.zerstoert) {
        this.kameraFehler.set(
          'Die Kamera konnte nicht gestartet werden. Bitte überprüfe die Kameraberechtigung.'
        );
      }
    } finally {
      this.kameraStartet = false;
    }
  }

  private kinokartePruefen(kinokartenId: string): void {
    this.pruefungLaeuft.set(true);

    this.einlass.kinokarteValidierenUndEntwerten(kinokartenId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (daten) => {
          this.pruefungLaeuft.set(false);

          this.ergebnisZustand.setzeErgebnis({
            typ: 'erfolg',
            saal: daten.saal,
            reihe: daten.reihe,
            sitzplatz: daten.sitzplatz
          });

          void this.router.navigate(['/ergebnis']);
        },
        error: (fehler: HttpErrorResponse) => {
          this.pruefungLaeuft.set(false);
          const ablehnung =
            fehler.status === 409 && this.istEinlassAblehnung(fehler.error)
              ? fehler.error
              : undefined;

          if (ablehnung) {
            console.error(
              `Einlass abgelehnt: ${ablehnung.grund} - ${ablehnung.meldung}`
            );
          }

          const technischerFehler =
            fehler.status === 0 || fehler.status >= 500;

          this.ergebnisZustand.setzeErgebnis({
            typ: 'fehler',
            titel: technischerFehler
              ? 'Technischer Fehler'
              : 'Einlass nicht möglich',
            beschreibung: technischerFehler
              ? 'Die Einlassprüfung konnte nicht abgeschlossen werden. Bitte versuche es erneut.'
              : ablehnung
                ? this.ablehnungsbeschreibung(ablehnung.grund)
                : 'Die Kinokarte wurde nicht akzeptiert.',
            ...(ablehnung
              ? {
                  grund: this.ablehnungsgrundAnzeigen(ablehnung.grund),
                  meldung: ablehnung.meldung
                }
              : {})
          });

          void this.router.navigate(['/ergebnis']);
        }
      });
  }

  private istEinlassAblehnung(
    fehler: unknown
  ): fehler is EinlassAblehnung {
    if (typeof fehler !== 'object' || fehler === null) {
      return false;
    }

    const ablehnung = fehler as Record<string, unknown>;
    return typeof ablehnung['grund'] === 'string'
      && typeof ablehnung['meldung'] === 'string';
  }

  private ablehnungsgrundAnzeigen(grund: string): string {
    const anzeigetexte: Record<string, string> = {
      ID_UNGUELTIG: 'Ungültige Ticket-ID',
      FALSCHE_VORSTELLUNG: 'Ungültige Vorstellung',
      ZU_FRUEH: 'Zu früh',
      ZU_SPAET: 'Zu spät',
      BEREITS_ENTWERTET: 'Bereits entwertet'
    };

    return anzeigetexte[grund] ?? grund;
  }

  private ablehnungsbeschreibung(grund: string): string {
    const beschreibungen: Record<string, string> = {
      ID_UNGUELTIG: 'Die Ticket-ID bzw. die verbundene Filmvorstellung ist unbekannt.',
      FALSCHE_VORSTELLUNG:
        'Diese Kinokarte gehört nicht zu dieser Filmvorstellung.',
      ZU_FRUEH:
        'Der Einlass ist noch nicht geöffnet.',
      ZU_SPAET:
        'Die Filmvorstellung ist bereits beendet.',
      BEREITS_ENTWERTET:
        'Diese Kinokarte wurde bereits entwertet und kann nicht erneut verwendet werden.'
    };

    return beschreibungen[grund] ?? 'Die Kinokarte wurde nicht akzeptiert.';
  }

  private kameraStoppen(): void {
    this.scanControls?.stop();
    this.scanControls = undefined;
    this.kameraLaeuft.set(false);
  }

  abmeldungAnzeigen(): void {
    this.kameraStoppen();
    this.abmeldungOffen.set(true);
  }

  abmeldungSchliessen(): void {
    this.abmeldungOffen.set(false);
    void this.kameraStarten();
  }

  ngOnDestroy(): void {
    this.zerstoert = true;
    this.kameraStoppen();
  }
}
