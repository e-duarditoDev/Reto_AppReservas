import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {

  nombre = '';
  email = '';
  password = '';
  password2 = '';

  error = '';
  loading = false;
  newsletter = false;
  registro() {
    this.error = '';

    if (!this.nombre || !this.email || !this.password) {
      this.error = 'Todos los campos son obligatorios';
      return;
    }

    if (this.password !== this.password2) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }

    this.loading = true;

    // simulación
    setTimeout(() => {
      console.log('Usuario registrado:', this.nombre);
      this.loading = false;
    }, 1000);
  }
}