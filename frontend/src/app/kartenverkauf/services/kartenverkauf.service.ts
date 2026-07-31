import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {
  Geldbetrag,
  Kartenbestellung,
  Kinokarte,
  Preisanfrage,
  Saalplan,
  Vorstellung,
  Verkaufsvorgang,
  Zahlungsstatus,
  Zahlungsvorgang,
  ZusammenhaengendePlaetze
} from '../dtos/kartenverkauf';

@Injectable({
  providedIn: 'root'
})
export class KartenverkaufService {
  private http = inject(HttpClient);

  private kartenverkaufUrl: string = '/api/kartenverkauf';

  private vorgaengeUrl: string = `${this.kartenverkaufUrl}/verkaufsvorgaenge`;

  public holeVorstellung(vorstellungId: string): Observable<Vorstellung> {
    return this.http.get<Vorstellung>(`${this.kartenverkaufUrl}/vorstellungen/${vorstellungId}`);
  }

  public holeSaalplan(vorstellungId: string): Observable<Saalplan> {
    return this.http.get<Saalplan>(`${this.kartenverkaufUrl}/saalplaene/${vorstellungId}`);
  }

  public sucheZusammenhaengendePlaetze(vorstellungId: string, platzanzahl: number): Observable<ZusammenhaengendePlaetze> {
    return this.http.get<ZusammenhaengendePlaetze>(`${this.kartenverkaufUrl}/saalplaene/${vorstellungId}/suche-zusammenhaengende-plaetze`, {
      params: {
        platzanzahl,
      }
    })
  }

  public ermittlePreis(vorstellungId: string, plaetze: ZusammenhaengendePlaetze): Observable<Geldbetrag> {
    const preisanfrage: Preisanfrage = {vorstellungId, plaetze};
    return this.http.post<Geldbetrag>(`${this.kartenverkaufUrl}/preisanfrage`, preisanfrage);
  }

  public starteVerkaufsvorgang(vorstellungId: string, plaetze: ZusammenhaengendePlaetze): Observable<Verkaufsvorgang> {
    const bestellung: Kartenbestellung = {vorstellungId, plaetze};
    return this.http.post<Verkaufsvorgang>(`${this.vorgaengeUrl}`, bestellung);
  }

  public starteZahlungsvorgang(auftragsnummer: string): Observable<Zahlungsvorgang> {
    return this.http.post<Zahlungsvorgang>(`${this.vorgaengeUrl}/${auftragsnummer}/zahlungsvorgaenge`, null);
  }

  public holeZahlungsStatus(auftragsnummer: string): Observable<Zahlungsstatus> {
    return this.http.get<Zahlungsstatus>(`${this.vorgaengeUrl}/${auftragsnummer}/zahlungsstatus`);
  }

  public holeKinokarten(auftragsnummer: string): Observable<Kinokarte[]> {
    return this.http.get<Kinokarte[]>(`${this.vorgaengeUrl}/${auftragsnummer}/kinokarten`);
  }
}
