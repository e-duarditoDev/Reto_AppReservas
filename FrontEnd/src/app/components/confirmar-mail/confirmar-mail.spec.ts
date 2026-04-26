import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmarMail } from './confirmar-mail';

describe('ConfirmarMail', () => {
  let component: ConfirmarMail;
  let fixture: ComponentFixture<ConfirmarMail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmarMail],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmarMail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
