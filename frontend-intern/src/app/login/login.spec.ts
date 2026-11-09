import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('soll die Login-Seite anzeigen', () => {
    const element = fixture.nativeElement;

    expect(element.querySelector('#username')).not.toBeNull();
    expect(element.querySelector('#password')).not.toBeNull();
    expect(element.querySelector('.login-button')).not.toBeNull();
  });

  it('soll bei leeren Eingaben eine Fehlermeldung anzeigen', () => {
    component.anmelden();
    fixture.detectChanges();

    expect(component.fehlermeldung).toBe(
      'Bitte Benutzername und Passwort eingeben.'
    );

    const fehlermeldung =
      fixture.nativeElement.querySelector('[role="alert"]');

    expect(fehlermeldung?.textContent).toContain(
      'Bitte Benutzername und Passwort eingeben.'
    );
  });

  it('soll ein fehlendes Passwort erkennen', () => {
    component.benutzername = 'mitarbeiter';
    component.passwort = '';

    component.anmelden();

    expect(component.fehlermeldung).toBe(
      'Bitte Benutzername und Passwort eingeben.'
    );
  });
});
