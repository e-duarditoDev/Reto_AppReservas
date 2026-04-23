import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-recuperar-password',
  standalone: true, 
  imports: [FormsModule,RouterModule],
  templateUrl: './recuperar-password.html',
  styleUrl: './recuperar-password.css',
})
export class RecuperarPassword {

  email = '';
  mensaje = '';

  enviar() {
    if (!this.email) return;

    this.mensaje = 'Se ha enviado un email a su bandeja de entrada';
  }
}