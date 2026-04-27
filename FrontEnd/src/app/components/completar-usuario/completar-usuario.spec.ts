import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompletarUsuario } from './completar-usuario';

describe('CompletarUsuario', () => {
  let component: CompletarUsuario;
  let fixture: ComponentFixture<CompletarUsuario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompletarUsuario],
    }).compileComponents();

    fixture = TestBed.createComponent(CompletarUsuario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
