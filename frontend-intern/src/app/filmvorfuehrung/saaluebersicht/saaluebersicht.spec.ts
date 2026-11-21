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

  it('soll den ganzen Tag von 00 bis 24 Uhr anzeigen', () => {
    const uhrzeiten = fixture.nativeElement.querySelectorAll('.zeitachse span');

    // 25 Uhrzeiten plus die Spaltenüberschrift "Saal".
    expect(uhrzeiten.length).toBe(26);
    expect(uhrzeiten[1].textContent).toBe('00:00');
    expect(uhrzeiten[25].textContent).toBe('24:00');
  });

  it('soll den Ausloggen-Dialog öffnen und schließen', () => {
    component.abmeldungAnzeigen();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.logout-dialog')).not.toBeNull();

    component.abmeldungSchliessen();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.logout-dialog')).toBeNull();
  });

  it('soll die aktuelle Position auf der 24-Stunden-Achse aktualisieren', () => {
    component.jetzt.set(new Date(2026, 10, 18, 14, 30));
    fixture.detectChanges();

    expect(component.uhrzeiten()[0]).toBe('00:00');
    expect(component.endUhrzeit()).toBe('24:00');
    expect(component.jetztPositionPx()).toBe(2420);

    component.jetzt.set(new Date(2026, 10, 18, 15, 0));
    fixture.detectChanges();

    expect(component.jetztPositionPx()).toBe(2500);
  });
});
