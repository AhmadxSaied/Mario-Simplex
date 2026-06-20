import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExitSimplex } from './exit-simplex';

describe('ExitSimplex', () => {
  let component: ExitSimplex;
  let fixture: ComponentFixture<ExitSimplex>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExitSimplex],
    }).compileComponents();

    fixture = TestBed.createComponent(ExitSimplex);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
