import {Component, DestroyRef, ElementRef, inject, output, signal, viewChild} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {interval, retry, switchMap, takeWhile} from 'rxjs';
import {GeldbetragPipe} from '../../../common/services/geldbetrag.pipe';
import {Zahlung} from '../../dtos/zahlung';
import {ZahlungService} from '../../services/zahlung.service';

@Component({
  selector: 'app-zahlungdialog',
  imports: [
    GeldbetragPipe
  ],
  templateUrl: './zahlungdialog.component.html',
  styleUrl: './zahlungdialog.component.css'
})
export class ZahlungdialogComponent {
  private zahlungService = inject(ZahlungService);
  private destroyRef = inject(DestroyRef);

  readonly bezahlt = output<void>();
  readonly abgebrochen = output<void>();

  readonly referenz = signal<string | undefined>(undefined);
  readonly zahlung = signal<Zahlung | undefined>(undefined);
  readonly verarbeitung = signal(false);
  readonly fehler = signal(false);

  readonly dialogRef = viewChild.required<ElementRef<HTMLDialogElement>>('zahlungDialogModal');

  oeffneDialog(referenz: string): void {
    this.referenz.set(referenz);
    this.zahlung.set(undefined);
    this.verarbeitung.set(false);
    this.fehler.set(false);
    this.dialogRef().nativeElement.showModal();
    // Die Zahlung wird beim Zahlungsvorgang asynchron registriert — kurz auf Verfügbarkeit warten.
    this.zahlungService.holeZahlung(referenz)
      .pipe(retry({count: 10, delay: 500}), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (zahlung) => this.zahlung.set(zahlung),
        error: () => this.fehler.set(true),
      });
  }

  bestaetige(): void {
    const referenz = this.referenz();
    if (!referenz) {
      return;
    }
    this.verarbeitung.set(true);
    this.fehler.set(false);
    this.zahlungService.bezahle(referenz)
      .pipe(
        switchMap(() => interval(1000).pipe(
          switchMap(() => this.zahlungService.holeZahlung(referenz)),
          takeWhile((zahlung) => zahlung.status === 'Offen', true)
        )),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (zahlung) => {
          if (zahlung.status === 'Eingegangen') {
            this.schliesse();
            this.bezahlt.emit();
          } else if (zahlung.status === 'Abgebrochen') {
            this.schliesse();
            this.abgebrochen.emit();
          }
        },
        error: () => {
          this.verarbeitung.set(false);
          this.fehler.set(true);
        },
      });
  }

  private schliesse(): void {
    this.verarbeitung.set(false);
    this.dialogRef().nativeElement.close();
  }
}
