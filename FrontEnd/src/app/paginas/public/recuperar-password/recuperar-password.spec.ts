import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecuperarPassword } from './recuperar-password';

describe('RecuperarPassword', () => {
  let component: RecuperarPassword;
  let fixture: ComponentFixture<RecuperarPassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecuperarPassword],
    }).compileComponents();

    fixture = TestBed.createComponent(RecuperarPassword);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
