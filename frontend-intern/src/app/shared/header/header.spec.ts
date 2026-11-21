import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { Header } from './header';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Header],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Header);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('zeigt das KinoSoft-Logo an', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('KinoSoft');
    expect(element.textContent).toContain('Intern');
  });

  it('zeigt den Menü-Button standardmäßig nicht an', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('.menue-button')).toBeNull();
  });

  it('meldet einen Klick auf den Menü-Button', () => {
    fixture.componentRef.setInput('menueAnzeigen', true);
    fixture.detectChanges();

    const emitSpy = spyOn(component.menueOeffnen, 'emit');
    const button = fixture.nativeElement.querySelector(
      '.menue-button'
    ) as HTMLButtonElement;

    button.click();

    expect(emitSpy).toHaveBeenCalled();
  });

  it('zeigt das Hauptmenü, wenn es geöffnet ist', () => {
    fixture.componentRef.setInput('menueAnzeigen', true);
    fixture.componentRef.setInput('menueOffen', true);
    fixture.detectChanges();

    const menu = fixture.nativeElement.querySelector('.hauptmenue');

    expect(menu).not.toBeNull();
    expect(menu.textContent).toContain('Einlasskontrolle');
    expect(menu.textContent).toContain('Filmvorführungen verwalten');
  });

  it('blendet die gerade aktive Seite aus dem Hauptmenü aus', () => {
    const router = TestBed.inject(Router);
    spyOnProperty(router, 'url', 'get').and.returnValue('/scanner');

    fixture.componentRef.setInput('menueAnzeigen', true);
    fixture.componentRef.setInput('menueOffen', true);
    fixture.detectChanges();

    const menu = fixture.nativeElement.querySelector('.hauptmenue');

    expect(menu.textContent).not.toContain('Einlasskontrolle');
    expect(menu.textContent).toContain('Filmvorführungen verwalten');
  });

  it('meldet einen Klick auf den Logout-Button', () => {
    const emitSpy = spyOn(component.abmelden, 'emit');
    const button = fixture.nativeElement.querySelector(
      '.logout-button'
    ) as HTMLButtonElement;

    button.click();

    expect(emitSpy).toHaveBeenCalled();
  });
});
