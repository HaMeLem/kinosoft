import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { Einlass } from './einlass';

describe('Einlass', () => {
  let service: Einlass;
  let httpMock: HttpTestingController;

  const apiUrl =
    '/api/intern/einlass/kinokarte-validieren-und-entwerten';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(Einlass);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('soll die Kinokarten-ID per POST senden', () => {
    const kinokartenId = '550e8400-e29b-41d4-a716-446655440000';

    service.kinokarteValidierenUndEntwerten(kinokartenId)
      .subscribe();

    const request = httpMock.expectOne(apiUrl);

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      kinokartenId
    });

    request.flush({
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    });
  });

  it('soll die Erfolgsantwort des Backends übernehmen', () => {
    const kinokartenId = '550e8400-e29b-41d4-a716-446655440000';

    const erwarteteAntwort = {
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    };

    service.kinokarteValidierenUndEntwerten(kinokartenId)
      .subscribe(antwort => {
        expect(antwort).toEqual(erwarteteAntwort);
      });

    const request = httpMock.expectOne(apiUrl);
    request.flush(erwarteteAntwort);
  });

  it('soll einen Backend-Fehler weitergeben', () => {
    const kinokartenId = '550e8400-e29b-41d4-a716-446655440000';

    service.kinokarteValidierenUndEntwerten(kinokartenId)
      .subscribe({
        error: fehler => {
          expect(fehler.status).toBe(500);
        }
      });

    const request = httpMock.expectOne(apiUrl);

    request.flush(null, {
      status: 500,
      statusText: 'Internal Server Error'
    });
  });
});
