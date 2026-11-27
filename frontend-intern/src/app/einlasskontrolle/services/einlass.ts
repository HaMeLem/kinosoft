import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import type { EinlassAbgelehnt } from '../dtos/einlassabgelehnt';
import type { Kinokarte } from '../dtos/kinokarte';

export type EinlassAntwort = Kinokarte | EinlassAbgelehnt;

@Injectable({
  providedIn: 'root',
})
export class Einlass {
  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    '/api/intern/einlass/kinokarte-validieren-und-entwerten';

  kinokarteValidierenUndEntwerten(
    kinokartenId: string
  ): Observable<EinlassAntwort> {
    return this.http.post<EinlassAntwort>(this.apiUrl, {
      kinokartenId,
    });
  }
}
