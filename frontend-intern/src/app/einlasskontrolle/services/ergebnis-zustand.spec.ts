import { TestBed } from '@angular/core/testing';
import { ErgebnisZustand } from './ergebnis-zustand';

describe('ErgebnisZustand', () => {
  let service: ErgebnisZustand;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErgebnisZustand);
  });

  it('soll anfangs kein Ergebnis enthalten', () => {
    expect(service.ergebnis()).toBeNull();
  });

  it('soll ein erfolgreiches Einlass-Ergebnis speichern', () => {
    const ergebnis = {
      typ: 'erfolg' as const,
      saal: 2,
      reihe: 5,
      sitzplatz: 8
    };

    service.setzeErgebnis(ergebnis);

    expect(service.ergebnis()).toEqual(ergebnis);
  });

  it('soll das gespeicherte Ergebnis löschen', () => {
    service.setzeErgebnis({
      typ: 'fehler',
      titel: 'Bereits entwertet'
    });

    service.loescheErgebnis();

    expect(service.ergebnis()).toBeNull();
  });
});
