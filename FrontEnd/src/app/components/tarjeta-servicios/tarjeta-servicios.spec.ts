import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TarjetaServicios } from './tarjeta-servicios';

describe('TarjetaServicios', () => {
  let component: TarjetaServicios;
  let fixture: ComponentFixture<TarjetaServicios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarjetaServicios],
    }).compileComponents();

    fixture = TestBed.createComponent(TarjetaServicios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
