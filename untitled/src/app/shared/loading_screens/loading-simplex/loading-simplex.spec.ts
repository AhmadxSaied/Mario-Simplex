import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingSimplex } from './loading-simplex';

describe('LoadingSimplex', () => {
  let component: LoadingSimplex;
  let fixture: ComponentFixture<LoadingSimplex>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingSimplex],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingSimplex);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
