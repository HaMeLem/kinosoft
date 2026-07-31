import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ZahlungComponent} from './zahlung.component';
import {KartenverkaufService} from '../../services/kartenverkauf.service';
import {Geldbetrag, Zahlungsstatus, Zahlungsvorgang} from '../../dtos/kartenverkauf';
import {of, throwError} from 'rxjs';

describe('ZahlungComponent', () => {
  let component: ZahlungComponent;
  let fixture: ComponentFixture<ZahlungComponent>;

  const kartenverkaufService = {
    ermittlePreis: vi.fn(() => of({betragInCent: 5000} as unknown as Geldbetrag)),
    starteVerkaufsvorgang: vi.fn(),
    starteZahlungsvorgang: vi.fn(),
    holeZahlungsStatus: vi.fn(),
  } as unknown as KartenverkaufService;

  beforeEach(async () => {
    vi.useFakeTimers();
    vi.mocked(kartenverkaufService.holeZahlungsStatus).mockReset();
    vi.mocked(kartenverkaufService.starteVerkaufsvorgang).mockReset();
    vi.mocked(kartenverkaufService.starteZahlungsvorgang).mockReset();

    await TestBed.configureTestingModule({
      providers: [
        {provide: KartenverkaufService, useValue: kartenverkaufService},
        ZahlungComponent
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZahlungComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('vorstellung', {
      uuid: '123e4567-e89b-12d3-a456-426614174000',
      beginn: '2025-03-16T20:00:00',
      saal: 'Saal 1',
      film: 'Inception',
    });
    fixture.componentRef.setInput('plaetze', {plaetze: [{reihe: 1, platz: 2}]});
    component.verkaufsvorgang.set({
      auftragsnummer: 'a1',
      vorstellungId: '123e4567-e89b-12d3-a456-426614174000',
      plaetze: {plaetze: [{reihe: 1, platz: 2}]},
      gesamtpreis: {betragInCent: 5000} as unknown as Geldbetrag,
    });
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  const zahlungsvorgang = (id: string, anlauf: number): Zahlungsvorgang =>
    ({id, anlauf, betrag: {betrag: 5000} as unknown as Geldbetrag, status: 'Ausstehend'});

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('beendet die Verarbeitung und meldet Bestätigung, sobald der Status Eingegangen ist', async () => {
    // arrange
    vi.mocked(kartenverkaufService.holeZahlungsStatus)
      .mockReturnValue(of({status: 'Eingegangen'} as Zahlungsstatus));
    let bestaetigt = false;
    component.onZahlungBestaetigt.subscribe(() => (bestaetigt = true));

    // act
    component.zahlungBezahlt();
    await vi.advanceTimersByTimeAsync(500);

    // assert
    expect(component.verarbeitung()).toBe(false);
    expect(component.fertig()).toBe(true);
    expect(bestaetigt).toBe(true);
  });

  it('beendet die Verarbeitung und markiert abgebrochen, wenn der Status Abgebrochen ist', async () => {
    // arrange
    vi.mocked(kartenverkaufService.holeZahlungsStatus)
      .mockReturnValue(of({status: 'Abgebrochen'} as Zahlungsstatus));

    // act
    component.zahlungBezahlt();
    await vi.advanceTimersByTimeAsync(500);

    // assert
    expect(component.verarbeitung()).toBe(false);
    expect(component.abgebrochen()).toBe(true);
    expect(component.fertig()).toBe(false);
  });

  it('öffnet den Dialog mit der Id des neuen Zahlungsvorgangs, ohne einen zweiten Vorgang anzulegen', () => {
    // arrange
    vi.mocked(kartenverkaufService.starteZahlungsvorgang).mockReturnValue(of(zahlungsvorgang('v2', 2)));
    const dialog = {oeffneDialog: vi.fn()};
    vi.spyOn(component, 'zahlungDialog').mockReturnValue(dialog as never);
    component.abgebrochen.set(true);

    // act
    component.bezahlen();

    // assert
    expect(kartenverkaufService.starteZahlungsvorgang).toHaveBeenCalledWith('a1');
    expect(kartenverkaufService.starteVerkaufsvorgang).not.toHaveBeenCalled();
    expect(dialog.oeffneDialog).toHaveBeenCalledWith('v2');
    expect(component.zahlungsvorgang()?.anlauf).toBe(2);
    expect(component.abgebrochen()).toBe(false);
  });

  it('startet keinen neuen Zahlungsvorgang, solange der laufende offen ist', () => {
    // arrange
    const dialog = {oeffneDialog: vi.fn()};
    vi.spyOn(component, 'zahlungDialog').mockReturnValue(dialog as never);
    vi.mocked(kartenverkaufService.starteZahlungsvorgang).mockReturnValue(of(zahlungsvorgang('v1', 1)));
    component.bezahlen();

    // act
    component.bezahlen();
    component.zahlungFortsetzen();

    // assert
    expect(kartenverkaufService.starteZahlungsvorgang).toHaveBeenCalledTimes(1);
    expect(component.zahlungLaeuft()).toBe(true);
    expect(dialog.oeffneDialog).toHaveBeenNthCalledWith(2, 'v1');
  });

  it('öffnet den Dialog bei der Wiederholung mit dem nächsten Zahlungsvorgang neu', () => {
    // arrange
    const dialog = {oeffneDialog: vi.fn()};
    vi.spyOn(component, 'zahlungDialog').mockReturnValue(dialog as never);
    vi.mocked(kartenverkaufService.starteZahlungsvorgang).mockReturnValueOnce(of(zahlungsvorgang('v1', 1)));
    component.bezahlen();
    component.zahlungAbgebrochen();

    // act
    vi.mocked(kartenverkaufService.starteZahlungsvorgang).mockReturnValueOnce(of(zahlungsvorgang('v2', 2)));
    component.bezahlen();

    // assert
    expect(dialog.oeffneDialog).toHaveBeenNthCalledWith(1, 'v1');
    expect(dialog.oeffneDialog).toHaveBeenNthCalledWith(2, 'v2');
    expect(component.zahlungsvorgang()?.id).toBe('v2');
  });

  it('legt beim ersten Zahlungsvorgang zuerst den Verkaufsvorgang an', () => {
    // arrange
    component.verkaufsvorgang.set(undefined);
    vi.mocked(kartenverkaufService.starteVerkaufsvorgang).mockReturnValue(of({
      auftragsnummer: 'neu1',
      vorstellungId: '123e4567-e89b-12d3-a456-426614174000',
      plaetze: {plaetze: [{reihe: 1, platz: 2}]},
      gesamtpreis: {betragInCent: 5000} as unknown as Geldbetrag,
    }));
    vi.mocked(kartenverkaufService.starteZahlungsvorgang).mockReturnValue(of(zahlungsvorgang('v1', 1)));
    const dialog = {oeffneDialog: vi.fn()};
    vi.spyOn(component, 'zahlungDialog').mockReturnValue(dialog as never);

    // act
    component.bezahlen();

    // assert
    expect(kartenverkaufService.starteVerkaufsvorgang).toHaveBeenCalled();
    expect(kartenverkaufService.starteZahlungsvorgang).toHaveBeenCalledWith('neu1');
    expect(component.verkaufsvorgang()?.auftragsnummer).toBe('neu1');
    expect(dialog.oeffneDialog).toHaveBeenCalledWith('v1');
  });

  it('beendet die Verarbeitung bei einem Fehler während des Pollings', async () => {
    // arrange
    vi.mocked(kartenverkaufService.holeZahlungsStatus)
      .mockReturnValue(throwError(() => new Error('Netzwerkfehler')));

    // act
    component.zahlungBezahlt();
    await vi.advanceTimersByTimeAsync(500);

    // assert
    expect(component.verarbeitung()).toBe(false);
    expect(component.abgebrochen()).toBe(true);
  });
});
