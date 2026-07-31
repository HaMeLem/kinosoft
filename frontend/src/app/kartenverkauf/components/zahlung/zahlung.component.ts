import {Component, DestroyRef, inject, input, OnInit, output, signal, viewChild} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {interval, Observable, of, switchMap, takeWhile, tap} from 'rxjs';
import {
  Geldbetrag,
  Verkaufsvorgang,
  Vorstellung,
  Zahlungsvorgang,
  ZusammenhaengendePlaetze
} from '../../dtos/kartenverkauf';
import {GeldbetragPipe} from '../../../common/services/geldbetrag.pipe';
import {ZahlungdialogComponent} from '../../../zahlung/components/zahlungdialog/zahlungdialog.component';
import {KartenverkaufService} from '../../services/kartenverkauf.service';

@Component({
  selector: 'app-zahlung',
  imports: [
    GeldbetragPipe,
    ZahlungdialogComponent
  ],
  templateUrl: './zahlung.component.html',
  styleUrl: './zahlung.component.css'
})
export class ZahlungComponent implements OnInit {
  private kartenverkaufService = inject(KartenverkaufService);
  private destroyRef = inject(DestroyRef);

  readonly vorstellung = input.required<Vorstellung>();

  readonly plaetze = input.required<ZusammenhaengendePlaetze>();

  readonly onZahlungBestaetigt = output<Verkaufsvorgang>();

  readonly gesamtbetrag = signal<Geldbetrag | undefined>(undefined);

  readonly verkaufsvorgang = signal<Verkaufsvorgang | undefined>(undefined);

  readonly zahlungsvorgang = signal<Zahlungsvorgang | undefined>(undefined);

  readonly fertig = signal(false);

  readonly verarbeitung = signal(false);

  readonly abgebrochen = signal(false);

  // Ein neuer Zahlungsvorgang ist erst erlaubt, wenn der laufende eingegangen oder gescheitert ist —
  // dieselbe Regel wie im Verkaufsvorgang. Schließt der Besucher den Dialog, wird er wieder geöffnet.
  readonly zahlungLaeuft = signal(false);

  readonly zahlungDialog = viewChild.required<ZahlungdialogComponent>('zahlungDialog');

  ngOnInit(): void {
    this.kartenverkaufService.ermittlePreis(this.vorstellung().uuid, this.plaetze())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((gesamtbetrag: Geldbetrag) => this.gesamtbetrag.set(gesamtbetrag));
  }

  bezahlen() {
    if (this.zahlungLaeuft()) {
      return;
    }
    this.abgebrochen.set(false);
    this.verkaufsvorgangAnlegenFallsNoetig()
      .pipe(
        switchMap((verkaufsvorgang) =>
          this.kartenverkaufService.starteZahlungsvorgang(verkaufsvorgang.auftragsnummer)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((zahlungsvorgang: Zahlungsvorgang) => {
        this.zahlungsvorgang.set(zahlungsvorgang);
        this.zahlungLaeuft.set(true);
        this.zahlungDialog().oeffneDialog(zahlungsvorgang.id);
      });
  }

  zahlungFortsetzen() {
    const laufender = this.zahlungsvorgang();
    if (laufender) {
      this.zahlungDialog().oeffneDialog(laufender.id);
    }
  }

  private verkaufsvorgangAnlegenFallsNoetig(): Observable<Verkaufsvorgang> {
    const vorhandener = this.verkaufsvorgang();
    if (vorhandener) {
      return of(vorhandener);
    }
    return this.kartenverkaufService.starteVerkaufsvorgang(this.vorstellung().uuid, this.plaetze())
      .pipe(tap((verkaufsvorgang) => this.verkaufsvorgang.set(verkaufsvorgang)));
  }

  // Der generische Dialog meldet die erfolgreiche Zahlung. Da der Kartenverkauf-Bestellstatus
  // asynchron (per Event) nachzieht, warten wir hier kurz auf "Eingegangen", bevor es weitergeht.
  zahlungBezahlt() {
    this.verarbeitung.set(true);
    interval(500)
      .pipe(
        switchMap(() => this.kartenverkaufService.holeZahlungsStatus(this.verkaufsvorgang()!.auftragsnummer)),
        takeWhile((zahlungsstatus) => zahlungsstatus.status === 'Ausstehend', true),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (zahlungsstatus) => {
          if (zahlungsstatus.status === 'Eingegangen') {
            this.verarbeitung.set(false);
            this.zahlungLaeuft.set(false);
            this.onZahlungBestaetigt.emit(this.verkaufsvorgang()!);
            this.fertig.set(true);
          } else if (zahlungsstatus.status === 'Abgebrochen') {
            this.verarbeitung.set(false);
            this.zahlungLaeuft.set(false);
            this.abgebrochen.set(true);
          }
        },
        error: () => {
          this.verarbeitung.set(false);
          this.zahlungLaeuft.set(false);
          this.abgebrochen.set(true);
        },
      });
  }

  zahlungAbgebrochen() {
    this.zahlungLaeuft.set(false);
    this.abgebrochen.set(true);
  }
}
