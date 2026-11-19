import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Header } from './header';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Header],
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

  it('meldet einen Klick auf den Logout-Button', () => {
    const emitSpy = spyOn(component.abmelden, 'emit');
    const button = fixture.nativeElement.querySelector(
      '.logout-button'
    ) as HTMLButtonElement;

    button.click();

    expect(emitSpy).toHaveBeenCalled();
  });
});
