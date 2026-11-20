import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilmvorfuehrungKachel } from './filmvorfuehrung-kachel';

describe('FilmvorfuehrungKachel', () => {
  let component: FilmvorfuehrungKachel;
  let fixture: ComponentFixture<FilmvorfuehrungKachel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FilmvorfuehrungKachel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FilmvorfuehrungKachel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
