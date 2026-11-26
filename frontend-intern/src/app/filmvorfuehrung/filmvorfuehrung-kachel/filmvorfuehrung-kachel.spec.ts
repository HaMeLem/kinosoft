import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilmvorfuehrungKachel } from './filmvorfuehrung-kachel';
import { FilmvorfuehrungDto } from '../dtos/filmvorfuehrungDto';

describe('FilmvorfuehrungKachel', () => {
  let component: FilmvorfuehrungKachel;
  let fixture: ComponentFixture<FilmvorfuehrungKachel>;

  const JETZT = new Date(2026, 10, 23, 12, 0, 0);

  function filmErstellen(sekunden: number): FilmvorfuehrungDto {
    const beginn = new Date(JETZT.getTime() + sekunden * 1000);
    const ende = new Date(beginn.getTime() + 2 * 60 * 60 * 1000);

    return {
      filmvorfuehrungId: 1,
      filmtitel: 'Film A',
      geplanterBeginn: beginn.toISOString(),
      geplantesEnde: ende.toISOString(),
      saal: 'Kleiner Saal',
      status: 'GEPLANT'
    };
  }

  function filmSetzen(sekunden: number) {
    component.filmvorfuehrung = filmErstellen(sekunden);
    component.aktuelleZeit = JETZT;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FilmvorfuehrungKachel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FilmvorfuehrungKachel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('zeigt 10 Minuten als 10:00 an', () => {
    filmSetzen(600);
    expect(component.countdown).toBe('10:00');
  });

  it('zeigt 90 Sekunden als 01:30 an', () => {
    filmSetzen(90);
    expect(component.countdown).toBe('01:30');
  });

  it('rundet die Restzeit auf', () => {
    filmSetzen(1.5);
    expect(component.countdown).toBe('00:02');
  });

  it('zeigt 00:00 genau zum Start', () => {
    filmSetzen(0);
    expect(component.countdown).toBe('00:00');
  });

  it('zeigt die Zeit nach dem Start an', () => {
    filmSetzen(-65);
    expect(component.countdown).toBe('01:05');
  });

  it('zeigt auch mehr als 60 Minuten richtig an', () => {
    filmSetzen(5400);
    expect(component.countdown).toBe('90:00');
  });

  it('zeigt 00:00, wenn keine Vorführung vorhanden ist', () => {
    component.filmvorfuehrung = undefined;
    component.aktuelleZeit = undefined;
    expect(component.countdown).toBe('00:00');
  });

});


