import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolverInput } from './solver-input';

describe('SolverInput', () => {
  let component: SolverInput;
  let fixture: ComponentFixture<SolverInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SolverInput],
    }).compileComponents();

    fixture = TestBed.createComponent(SolverInput);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
