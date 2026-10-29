import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Scanner } from './scanner';

describe('Scanner', () => {
  let component: Scanner;
  let fixture: ComponentFixture<Scanner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Scanner],
    }).compileComponents();

    fixture = TestBed.createComponent(Scanner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

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
});
