import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Saaluebersicht } from './saaluebersicht';

describe('Saaluebersicht', () => {
  let component: Saaluebersicht;
  let fixture: ComponentFixture<Saaluebersicht>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Saaluebersicht]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Saaluebersicht);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
