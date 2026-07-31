import {TestBed} from '@angular/core/testing';

import {KartenverkaufService} from './kartenverkauf.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';

describe('KartenverkaufService', () => {
  let service: KartenverkaufService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        KartenverkaufService
      ]
    });
    service = TestBed.inject(KartenverkaufService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('holeZahlungsStatus ruft den Status-Endpunkt per GET auf', () => {
    const auftragsnummer = '11111111-1111-1111-1111-111111111111';

    service.holeZahlungsStatus(auftragsnummer).subscribe(status => {
      expect(status.status).toBe('Eingegangen');
    });

    const req = httpMock.expectOne(`/api/kartenverkauf/verkaufsvorgaenge/${auftragsnummer}/zahlungsstatus`);

    expect(req.request.method).toBe('GET');
    req.flush({status: 'Eingegangen'});
  });

  it('starteVerkaufsvorgang legt den Vorgang mit Bestellung als Body an', () => {
    const plaetze = {plaetze: [{reihe: 4, platz: 1}]};

    service.starteVerkaufsvorgang('090c173a-3636-4980-865a-1ec859eb4f90', plaetze).subscribe();

    const req = httpMock.expectOne('/api/kartenverkauf/verkaufsvorgaenge');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({vorstellungId: '090c173a-3636-4980-865a-1ec859eb4f90', plaetze});
    req.flush(null);
  });

  it('starteZahlungsvorgang ruft die Zahlungsvorgänge des Vorgangs ohne Body per POST auf', () => {
    const auftragsnummer = '11111111-1111-1111-1111-111111111111';

    service.starteZahlungsvorgang(auftragsnummer).subscribe(zahlungsvorgang => {
      expect(zahlungsvorgang.anlauf).toBe(2);
    });

    const req = httpMock.expectOne(`/api/kartenverkauf/verkaufsvorgaenge/${auftragsnummer}/zahlungsvorgaenge`);

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush({id: 'v2', anlauf: 2, betrag: {betrag: 5000, waehrung: 'EUR'}, status: 'Ausstehend'});
  });
});
