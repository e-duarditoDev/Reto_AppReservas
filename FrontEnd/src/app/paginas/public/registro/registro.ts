import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, Location } from '@angular/common';
import { AuthService } from '../../../servicios/auth-service';
import { ChangeDetectorRef } from '@angular/core';

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
  private cd = inject(ChangeDetectorRef);

  constructor(private location: Location) {}

  volver() {
    this.location.back();
  }

  registro() {
    this.error = '';
    this.mensajeOk = '';

    if (this.password !== this.password2) {
      this.error = 'Las contraseñas no coinciden.';
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


    this.authService.confirmarEmail(this.email, this.password).subscribe({
      next: () => {
        console.log('next ejecutado');
        this.mensajeOk = 'Revisa tu correo para confirmar tu cuenta.';
        this.loading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.error = typeof err.error === 'string' ? err.error : 'Ha ocurrido un error, intentalo de nuevo.';
        this.loading = false;
        this.cd.detectChanges();

      }
    });

    // simulación
    /*     setTimeout(() => {
          console.log('Usuario registrado:', this.nombre);
          this.loading = false;
        }, 1000); */

  }
}