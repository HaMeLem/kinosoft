import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface KinokartenDTO {
  saal: number;
  reihe: number;
  sitzplatz: number;
}

@Injectable({
  providedIn: 'root',
})
export class Einlass {
  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    '/api/intern/einlass/kinokarte-validieren-und-entwerten';

  kinokarteValidierenUndEntwerten(
    kinokartenId: string
  ): Observable<KinokartenDTO> {
    return this.http.post<KinokartenDTO>(this.apiUrl, {
      kinokartenId,
    });
  }
}
