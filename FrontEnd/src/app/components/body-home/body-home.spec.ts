import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BodyHome } from './body-home';

describe('BodyHome', () => {
  let component: BodyHome;
  let fixture: ComponentFixture<BodyHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BodyHome],
    }).compileComponents();

    fixture = TestBed.createComponent(BodyHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
