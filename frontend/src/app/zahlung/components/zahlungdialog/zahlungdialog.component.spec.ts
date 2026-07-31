import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ZahlungdialogComponent} from './zahlungdialog.component';
import {ZahlungService} from '../../services/zahlung.service';
import {Zahlung} from '../../dtos/zahlung';
import {of, throwError} from 'rxjs';

describe('ZahlungdialogComponent', () => {
  let component: ZahlungdialogComponent;
  let fixture: ComponentFixture<ZahlungdialogComponent>;

  const zahlungService = {
    holeZahlung: vi.fn(),
    bezahle: vi.fn(),
  } as unknown as ZahlungService;

  function zahlungMit(status: Zahlung['status']): Zahlung {
    return {referenz: 'r1', betragInCent: 5000, status};
  }

  beforeEach(async () => {
    vi.useFakeTimers();
    vi.mocked(zahlungService.holeZahlung).mockReset();
    vi.mocked(zahlungService.bezahle).mockReset();

    await TestBed.configureTestingModule({
      imports: [ZahlungdialogComponent],
      providers: [
        {provide: ZahlungService, useValue: zahlungService}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ZahlungdialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    // jsdom implementiert showModal/close nicht zuverlässig — für den Test stubben.
    const dialog = fixture.nativeElement.querySelector('dialog') as HTMLDialogElement;
    dialog.showModal = vi.fn();
    dialog.close = vi.fn();
    component.referenz.set('r1');
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('meldet bezahlt und beendet die Verarbeitung, sobald der Status Eingegangen ist', async () => {
    // arrange
    vi.mocked(zahlungService.bezahle).mockReturnValue(of(undefined));
    vi.mocked(zahlungService.holeZahlung).mockReturnValue(of(zahlungMit('Eingegangen')));
    let bezahlt = false;
    component.bezahlt.subscribe(() => (bezahlt = true));

    // act
    component.bestaetige();
    await vi.advanceTimersByTimeAsync(1000);

    // assert
    expect(bezahlt).toBe(true);
    expect(component.verarbeitung()).toBe(false);
    expect(component.fehler()).toBe(false);
  });

  it('meldet abgebrochen, wenn der Status Abgebrochen ist', async () => {
    // arrange
    vi.mocked(zahlungService.bezahle).mockReturnValue(of(undefined));
    vi.mocked(zahlungService.holeZahlung).mockReturnValue(of(zahlungMit('Abgebrochen')));
    let abgebrochen = false;
    component.abgebrochen.subscribe(() => (abgebrochen = true));

    // act
    component.bestaetige();
    await vi.advanceTimersByTimeAsync(1000);

    // assert
    expect(abgebrochen).toBe(true);
    expect(component.verarbeitung()).toBe(false);
  });

  it('macht den Dialog bei einem Fehler wieder bedienbar', async () => {
    // arrange
    vi.mocked(zahlungService.bezahle).mockReturnValue(of(undefined));
    vi.mocked(zahlungService.holeZahlung).mockReturnValue(throwError(() => new Error('Netzwerkfehler')));

    // act
    component.bestaetige();
    await vi.advanceTimersByTimeAsync(1000);

    // assert
    expect(component.verarbeitung()).toBe(false);
    expect(component.fehler()).toBe(true);
  });
});
