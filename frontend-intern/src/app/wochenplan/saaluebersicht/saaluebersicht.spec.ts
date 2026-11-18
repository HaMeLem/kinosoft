import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Saaluebersicht } from './saaluebersicht';

registerLocaleData(localeDe);

describe('Saaluebersicht', () => {
  let component: Saaluebersicht;
  let fixture: ComponentFixture<Saaluebersicht>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Saaluebersicht],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Saaluebersicht);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('soll die Saaluebersicht erstellen', () => {
    expect(component).toBeTruthy();
  });

  it('soll beide Kinosaele anzeigen', () => {
    const saele = fixture.nativeElement.querySelectorAll('.saalzeile h2');

    expect(saele.length).toBe(2);
    expect(saele[0].textContent).toContain('Kleiner Saal');
    expect(saele[1].textContent).toContain('Großer Saal');
  });

  it('soll neun Stunden in der Zeitachse anzeigen', () => {
    const uhrzeiten = fixture.nativeElement.querySelectorAll('.zeitachse span');

    // Neun Uhrzeiten plus die Spaltenüberschrift "Saal".
    expect(uhrzeiten.length).toBe(10);
  });

  it('soll den Ausloggen-Dialog öffnen und schließen', () => {
    component.abmeldungAnzeigen();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.logout-dialog')).not.toBeNull();

    component.abmeldungSchliessen();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.logout-dialog')).toBeNull();
  });
});
