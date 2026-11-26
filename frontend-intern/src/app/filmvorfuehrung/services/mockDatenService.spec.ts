
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { MockDatenService } from './mockDatenService';
import { SaalService } from './saal';

describe('MockDatenService', () => {

  let service: MockDatenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MockDatenService);
  });

  // Prüft, ob der Service erstellt wird
  it('sollte erstellt werden', () => {
    expect(service).toBeTruthy();
  });

  // Prüft, ob Filme vorhanden sind
  it('sollte Filmvorführungen liefern', () => {
    const filme = service.getFilmvorfuehrungen();

    expect(filme.length).toBeGreaterThan(0);
  });

  // Prüft, ob alle Säle gültig sind
  it('sollte nur bekannte Saalnamen verwenden', async () => {
    const saalService = TestBed.inject(SaalService);

    const saele = await firstValueFrom(saalService.holeSaele());

    const saalNamen = saele.map(saal => saal.name);

    const filme = service.getFilmvorfuehrungen();

    filme.forEach(film => {
      expect(saalNamen).toContain(film.saal);
    });
  });

  // Prüft, ob die Filme im richtigen Saal laufen
  it('sollte Filme den richtigen Sälen zuordnen', () => {
    const filme = service.getFilmvorfuehrungen();

    const filmA = filme.find(film => film.filmtitel === 'Film A');
    const filmB = filme.find(film => film.filmtitel === 'Film B');
    const filmC = filme.find(film => film.filmtitel === 'Film C');
    const filmD = filme.find(film => film.filmtitel === 'Film D');

    expect(filmA).toBeDefined();
    expect(filmB).toBeDefined();
    expect(filmC).toBeDefined();
    expect(filmD).toBeDefined();

    expect(filmA!.saal).toBe('Kleiner Saal');
    expect(filmB!.saal).toBe('Kleiner Saal');
    expect(filmC!.saal).toBe('Großer Saal');
    expect(filmD!.saal).toBe('Großer Saal');
  });

});

