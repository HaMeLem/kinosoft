import {Component, DestroyRef, inject, OnInit, output, input, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Kinokarte, Verkaufsvorgang} from '../../dtos/kartenverkauf';
import {KartenverkaufService} from '../../services/kartenverkauf.service';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-kinokarte',
  imports: [
    DatePipe
],
  templateUrl: './kinokarte.component.html',
  styleUrl: './kinokarte.component.css'
})
export class KinokarteComponent implements OnInit {
  private kartenverkaufService = inject(KartenverkaufService);
  private destroyRef = inject(DestroyRef);

  readonly zahlungsbestaetigung = input.required<Verkaufsvorgang>();

  readonly onKinokartenGedruckt = output<Kinokarte[]>();

  readonly kinokarten = signal<Kinokarte[] | undefined>(undefined);

  readonly stacked = signal(true);

  ngOnInit(): void {
    const z = this.zahlungsbestaetigung();
    this.kartenverkaufService.holeKinokarten(z.auftragsnummer)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data: Kinokarte[]) => this.kinokarten.set(data));
  }

  druckeKinokarten() {
    this.onKinokartenGedruckt.emit(this.kinokarten()!);
  }

  toggleStacked() {
    this.stacked.update(value => !value);
  }
}
