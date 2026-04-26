import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, Location } from '@angular/common';

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
  mensajeOk = '';
  loading = false;
  newsletter = false;

  constructor(private location: Location) {}

  volver() {
    this.location.back();
  }

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

    const datosRegistro = {
      email: this.email,
      password: this.password,
    };


    this.authService.confirmarEmail(this.email, this.password).subscribe({
      next: () => {
        this.mensajeOk = 'Revisa tu correo para confirmar tu cuenta.';
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error || 'Ha ocurrido un error, intentalo de nuevo.';
        this.loading = false;
      }
    });

    // simulación
/*     setTimeout(() => {
      console.log('Usuario registrado:', this.nombre);
      this.loading = false;
    }, 1000); */

  }
}