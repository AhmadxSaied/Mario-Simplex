import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SoolverResult } from './soolver-result';

describe('SoolverResult', () => {
  let component: SoolverResult;
  let fixture: ComponentFixture<SoolverResult>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoolverResult],
    }).compileComponents();

    fixture = TestBed.createComponent(SoolverResult);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
