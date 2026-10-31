import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Ergebnis } from './ergebnis';
import { ErgebnisZustand } from '../services/ergebnis-zustand';

describe('Ergebnis', () => {
  let fixture: ComponentFixture<Ergebnis>;
  let ergebnisZustand: ErgebnisZustand;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Ergebnis],
    }).compileComponents();

    ergebnisZustand = TestBed.inject(ErgebnisZustand);
    fixture = TestBed.createComponent(Ergebnis);
  });

  it('soll bei Erfolg Saal, Reihe und Platz anzeigen', () => {
    ergebnisZustand.setzeErgebnis({
      typ: 'erfolg',
      saal: 2,
      reihe: 5,
      sitzplatz: 8,
    });

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    const details = element.querySelectorAll('.ticket-detail');

    expect(element.querySelector('h1')?.textContent)
      .toContain('Einlass erlaubt');

    expect(details.length).toBe(3);
    expect(details[0].textContent).toContain('Saal');
    expect(details[0].textContent).toContain('2');
    expect(details[1].textContent).toContain('5');
    expect(details[2].textContent).toContain('8');
  });

  it('soll bei Ablehnung die Fehlermeldung anzeigen', () => {
    ergebnisZustand.setzeErgebnis({
      typ: 'fehler',
      titel: 'Bereits entwertet',
      beschreibung: 'Diese Kinokarte wurde bereits entwertet.',
    });

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('h1')?.textContent)
      .toContain('Einlass nicht erlaubt');

    expect(element.querySelector('[role="alert"]')?.textContent)
      .toContain('Bereits entwertet');

    expect(element.querySelector('.ticket-details')).toBeNull();
  });

  it('soll den Logout-Dialog öffnen und schließen', () => {
    ergebnisZustand.setzeErgebnis({
      typ: 'erfolg',
      saal: 2,
      reihe: 5,
      sitzplatz: 8,
    });

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    const logoutButton =
      element.querySelector<HTMLButtonElement>('.logout-button');

    logoutButton?.click();
    fixture.detectChanges();

    expect(element.querySelector('[role="dialog"]')).not.toBeNull();

    const abbrechenButton =
      element.querySelector<HTMLButtonElement>('.logout-cancel');

    abbrechenButton?.click();
    fixture.detectChanges();

    expect(element.querySelector('[role="dialog"]')).toBeNull();
  });
});
