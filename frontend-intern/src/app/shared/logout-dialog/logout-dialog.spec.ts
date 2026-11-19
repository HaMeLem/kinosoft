import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LogoutDialog } from './logout-dialog';

describe('LogoutDialog', () => {
  let component: LogoutDialog;
  let fixture: ComponentFixture<LogoutDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogoutDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(LogoutDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('zeigt die Logout-Bestätigung an', () => {
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain(
      'Möchtest du dich wirklich ausloggen?'
    );
    expect(element.querySelector('[role="dialog"]')).not.toBeNull();
  });

  it('meldet das Schließen über den X-Button', () => {
    const emitSpy = spyOn(component.schliessen, 'emit');
    const button = fixture.nativeElement.querySelector(
      '.logout-close'
    ) as HTMLButtonElement;

    button.click();

    expect(emitSpy).toHaveBeenCalledTimes(1);
  });

  it('meldet das Schließen über den Abbrechen-Button', () => {
    const emitSpy = spyOn(component.schliessen, 'emit');
    const button = fixture.nativeElement.querySelector(
      '.logout-cancel'
    ) as HTMLButtonElement;

    button.click();

    expect(emitSpy).toHaveBeenCalledTimes(1);
  });

  it('enthält den Link zum Ausloggen', () => {
    const link = fixture.nativeElement.querySelector(
      '.logout-confirm'
    ) as HTMLAnchorElement;

    expect(link.textContent).toContain('Ja, ausloggen');
    expect(link.getAttribute('href')).toBe('/');
  });
});
