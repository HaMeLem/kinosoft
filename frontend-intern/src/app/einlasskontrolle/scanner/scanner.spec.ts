import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { BrowserQRCodeReader, IScannerControls } from '@zxing/browser';

import { Scanner } from './scanner';
import { ErgebnisZustand } from '../services/ergebnis-zustand';

type ScanCallback =
  Parameters<BrowserQRCodeReader['decodeFromVideoDevice']>[2];

describe('Scanner', () => {
  let component: Scanner;
  let fixture: ComponentFixture<Scanner>;
  let httpTesting: HttpTestingController;
  let ergebnisZustand: ErgebnisZustand;

  let scanCallback: ScanCallback;
  let scanControls: IScannerControls;
  let kameraStoppenSpy: jasmine.Spy;
  let navigationSpy: jasmine.Spy;

  const testId = '550e8400-e29b-41d4-a716-446655440000';
  const apiUrl =
    '/api/intern/einlass/kinokarte-validieren-und-entwerten';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Scanner],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    ergebnisZustand = TestBed.inject(ErgebnisZustand);

    navigationSpy = spyOn(
      TestBed.inject(Router),
      'navigate'
    ).and.resolveTo(true);

    kameraStoppenSpy = jasmine.createSpy('kameraStoppen');
    scanControls = { stop: kameraStoppenSpy };

    // Simuliert die Kamera, ohne auf echte Hardware zuzugreifen.
    spyOn(
      BrowserQRCodeReader.prototype,
      'decodeFromVideoDevice'
    ).and.callFake(async (_deviceId, _video, callback) => {
      scanCallback = callback;
      return scanControls;
    });

    fixture = TestBed.createComponent(Scanner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  function qrCodeErkennen(inhalt: string): void {
    const qrErgebnis = {
      getText: () => inhalt
    } as NonNullable<Parameters<ScanCallback>[0]>;
    scanCallback(qrErgebnis, undefined, scanControls);
  }

  it('soll die Scanner-Seite anzeigen', () => {
    const element = fixture.nativeElement;
    expect(element.querySelector('h1')?.textContent)
      .toContain('QR-Code scannen');
    expect(element.querySelector('.camera-preview'))
      .not.toBeNull();
    expect(element.querySelector('.logout-button'))
      .not.toBeNull();
  });

  it('soll den Logout-Dialog öffnen', () => {
    const logoutButton: HTMLButtonElement =
      fixture.nativeElement.querySelector('.logout-button');

    logoutButton.click();
    fixture.detectChanges();

    expect(component.abmeldungOffen()).toBeTrue();

    expect(fixture.nativeElement.querySelector('[role="dialog"]'))
      .not.toBeNull();
  });

  it('soll den Logout-Dialog beim Abbrechen schließen', () => {
    component.abmeldungAnzeigen();
    fixture.detectChanges();

    const abbrechenButton: HTMLButtonElement =
      fixture.nativeElement.querySelector('.logout-cancel');

    abbrechenButton.click();
    fixture.detectChanges();

    expect(component.abmeldungOffen()).toBeFalse();

    expect(fixture.nativeElement.querySelector('[role="dialog"]'))
      .toBeNull();
  });

  it('soll die Kamera beim Öffnen automatisch starten', async () => {
    await fixture.whenStable();

    expect(
      BrowserQRCodeReader.prototype.decodeFromVideoDevice
    ).toHaveBeenCalledTimes(1);

    expect(component.kameraLaeuft()).toBeTrue();

    expect(
      fixture.nativeElement.querySelector('.kamera-start-button')
    ).toBeNull();
  });

  it('soll eine gültige QR-ID an das Backend senden', async () => {
    await component.kameraStarten();

    qrCodeErkennen(testId);

    const anfrage = httpTesting.expectOne(apiUrl);

    expect(anfrage.request.method).toBe('POST');
    expect(anfrage.request.body).toEqual({
      kinokartenId: testId
    });

    expect(component.pruefungLaeuft()).toBeTrue();
    expect(kameraStoppenSpy).toHaveBeenCalled();

    anfrage.flush({
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    });

    expect(ergebnisZustand.ergebnis()).toEqual({
      typ: 'erfolg',
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    });

    expect(component.pruefungLaeuft()).toBeFalse();
    expect(navigationSpy).toHaveBeenCalledWith(['/ergebnis']);
  });

  it('soll nach einem ungültigen QR-Code weiterscannen', async () => {
    await component.kameraStarten();

    qrCodeErkennen('kein-gueltiges-ticket');

    httpTesting.expectNone(apiUrl);

    expect(component.kameraFehler())
      .toContain('Ungültiger QR-Code');

    expect(kameraStoppenSpy).not.toHaveBeenCalled();

    qrCodeErkennen(testId);

    const anfrage = httpTesting.expectOne(apiUrl);

    expect(anfrage.request.body).toEqual({
      kinokartenId: testId
    });

    expect(kameraStoppenSpy).toHaveBeenCalled();

    anfrage.flush({
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    });

    expect(ergebnisZustand.ergebnis()).toEqual({
      typ: 'erfolg',
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    });

    expect(navigationSpy).toHaveBeenCalledWith(['/ergebnis']);
  });

  it('soll bei einer Backend-Ablehnung ein Fehlerergebnis speichern', async () => {
    await component.kameraStarten();

    qrCodeErkennen(testId);

    const anfrage = httpTesting.expectOne(apiUrl);

    anfrage.flush(
      { message: 'Ticket abgelehnt' },
      { status: 400, statusText: 'Bad Request' }
    );

    expect(ergebnisZustand.ergebnis()).toEqual({
      typ: 'fehler',
      titel: 'Einlass nicht möglich',
      beschreibung: 'Die Kinokarte wurde nicht akzeptiert.'
    });

    expect(navigationSpy).toHaveBeenCalledWith(['/ergebnis']);
  });

  it('soll bei einem Serverfehler eine technische Fehlermeldung speichern', async () => {
    await component.kameraStarten();

    qrCodeErkennen(testId);

    const anfrage = httpTesting.expectOne(apiUrl);

    anfrage.flush(
      { message: 'Serverfehler' },
      { status: 500, statusText: 'Internal Server Error' }
    );

    expect(ergebnisZustand.ergebnis()).toEqual({
      typ: 'fehler',
      titel: 'Technischer Fehler',
      beschreibung:
        'Die Einlassprüfung konnte nicht abgeschlossen werden. Bitte versuche es erneut.'
    });

    expect(navigationSpy).toHaveBeenCalledWith(['/ergebnis']);
  });

  it('soll denselben Scan nicht mehrfach senden', async () => {
    await component.kameraStarten();

    qrCodeErkennen(testId);
    qrCodeErkennen(testId);

    const anfrage = httpTesting.expectOne(apiUrl);

    expect(anfrage.request.method).toBe('POST');

    anfrage.flush({
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    });

    expect(navigationSpy).toHaveBeenCalledTimes(1);
  });
});
