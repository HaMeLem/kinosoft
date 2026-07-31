import {Component, DestroyRef, inject, OnInit, output, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {DatePipe} from '@angular/common';
import {DatumService} from '../../../common/services/datum.service';
import {format, isBefore, startOfDay} from 'date-fns';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {distinctUntilChanged, map} from 'rxjs';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-kalender',
  imports: [
    DatePipe,
    FormsModule
],
  templateUrl: './kalender.component.html',
  styleUrl: './kalender.component.css'
})
export class KalenderComponent implements OnInit {
  private datumService = inject(DatumService);
  private router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private destroyRef = inject(DestroyRef);

  readonly today: Date = startOfDay(new Date());

  readonly selectableDates = signal<Date[]>([this.today]);

  readonly selectedDate = signal<Date>(this.today);

  readonly dateSelected = output<Date>();

  ngOnInit(): void {
    this.selectableDates.set(this.datumService.getWochentage(this.today));
    this.activatedRoute.queryParamMap.pipe(
      map((queryParamMap) => queryParamMap.get('datum')),
      map((value) => value ? new Date(value) : this.today),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        const day = startOfDay(value ?? this.today);
        this.selectedDate.set(day);
        this.dateSelected.emit(day);
      });
  }

  selectDate(datum: Date): void {
    this.navigate({'datum': format(datum, 'yyyy-MM-dd')});
  }

  isBeforeToday(date: Date): boolean {
    return isBefore(startOfDay(date), this.today);
  }

  navigate(queryParams: Params): Promise<boolean> {
    return this.router.navigate([], {
      queryParams, queryParamsHandling: 'merge', replaceUrl: true,
    });
  }
}
