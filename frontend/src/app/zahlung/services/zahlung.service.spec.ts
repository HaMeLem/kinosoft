import {TestBed} from '@angular/core/testing';

import {ZahlungService} from './zahlung.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';

describe('ZahlungService', () => {
  let service: ZahlungService;
  let httpMock: HttpTestingController;

  const referenz = '11111111-1111-1111-1111-111111111111';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ZahlungService
      ]
    });
    service = TestBed.inject(ZahlungService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('holeZahlung ruft den Zahlungs-Endpunkt per GET auf', () => {
    service.holeZahlung(referenz).subscribe(zahlung => {
      expect(zahlung.status).toBe('Offen');
      expect(zahlung.betragInCent).toBe(5000);
    });

    const req = httpMock.expectOne(`/api/zahlung/${referenz}`);
    expect(req.request.method).toBe('GET');
    req.flush({referenz, betragInCent: 5000, status: 'Offen'});
  });

  it('bezahle stößt die Zahlung per POST an', () => {
    service.bezahle(referenz).subscribe();

    const req = httpMock.expectOne(`/api/zahlung/${referenz}/bezahlen`);
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });
});
