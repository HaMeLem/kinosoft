import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Zahlung} from '../dtos/zahlung';

@Injectable({
  providedIn: 'root'
})
export class ZahlungService {
  private http = inject(HttpClient);

  private zahlungUrl: string = '/api/zahlung';

  public holeZahlung(referenz: string): Observable<Zahlung> {
    return this.http.get<Zahlung>(`${this.zahlungUrl}/${referenz}`);
  }

  public bezahle(referenz: string): Observable<void> {
    return this.http.post<void>(`${this.zahlungUrl}/${referenz}/bezahlen`, {});
  }
}
