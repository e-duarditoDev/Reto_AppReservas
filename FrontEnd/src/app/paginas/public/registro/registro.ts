import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, Location } from '@angular/common';
import { AuthService } from '../../../servicios/auth-service';

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

  private authService = inject(AuthService);

  constructor(private location: Location) {}

  volver() {
    this.location.back();
  }

  registro() {
    this.error = '';
    this.mensajeOk = '';

    // VALIDACIONES
    if (!this.nombre || !this.email || !this.password) {
      this.error = 'Todos los campos son obligatorios';
      return;
    }

    if (this.password !== this.password2) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }

    this.loading = true;

    // OBJETO QUE ENVÍAS AL BACKEND
    const datosRegistro = {
      nombre: this.nombre,
      email: this.email,
      password: this.password,
      newsletter: this.newsletter
    };

    // LLAMADA REAL
    // this.authService.confirmarEmail(datosRegistro).subscribe({
    //   next: () => {
    //     this.mensajeOk = 'Revisa tu correo para confirmar tu cuenta.';
    //     this.loading = false;
    //   },
    //   error: (err) => {
    //     this.error = err.error || 'Ha ocurrido un error, inténtalo de nuevo.';
    //     this.loading = false;
    //   }
    // });
  }
}