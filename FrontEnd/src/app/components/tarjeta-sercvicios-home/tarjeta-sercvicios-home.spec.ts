import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TarjetaSercviciosHome } from './tarjeta-sercvicios-home';

describe('TarjetaSercviciosHome', () => {
  let component: TarjetaSercviciosHome;
  let fixture: ComponentFixture<TarjetaSercviciosHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarjetaSercviciosHome],
    }).compileComponents();

    fixture = TestBed.createComponent(TarjetaSercviciosHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
