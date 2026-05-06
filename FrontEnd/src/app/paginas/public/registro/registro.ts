import { Component, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { AuthService } from '../../../servicios/auth-service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {

  nombre = '';
  email = '';
  password = '';
  password2 = '';

  error = '';
  ok = '';
  loading = false;
  newsletter = false;


  //AuthService es un servicio creado por mi para la autenticacion
  private authService = inject(AuthService);

  // Constructor para el Location que permite volver a la pagina antetrior
  constructor(private location: Location) { }

  // funcion de volver
  volver() {
    this.location.back();
  }

  // Metodo realiza el registro.
  registro() {

    //Variables de control de errores y mensajes de éxito
    this.error = '';
    this.ok = '';

    if (this.newsletter) {
      console.log("Newsletter activado")
    } else {
      console.log("Newsletter desactivado")
    }

    if (this.email.trim() === '') {
      this.error = 'El email es obligatorio.';
      return;
    }

    // Validacion, si el email no tiene un @, error
    if (!this.email.includes('@')) {
      this.error = 'Email en formato incorrecto.';
      return;
    }

    if (this.password.length < 4) {
      this.error = 'La contraseña debe tener al menos 4 caracteres.';
      return;
    }


    if (this.password2.length < 4) {
      this.error = 'La contraseña debe tener al menos 4 caracteres.';
      return;
    }

    if (this.password !== this.password2) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }

    this.loading = true;

    // DTO se envia al backend con los datos del formulario
    const dtoRegistro = {
      nombre: this.nombre,
      email: this.email,
      password: this.password,
      newsletter: this.newsletter
    };


    //Suscribe escucha lo que emite el Observable del servicio AuthService.confirmarEmail, sin suscribe observable no se ejecuta
    this.authService.confirmarEmail(this.email, this.password).subscribe({
      next: () => {
        console.log('Se han enviado datos a la API.');
        this.ok = 'Revisa tu correo para confirmar tu cuenta.';
        this.loading = false;
      },
      error: (err) => {
        /* 
          typeof devuelve el tipo de una variable, err es el objeto ded error devuelve la API compuesto por statatus y error,
          err.error toma el mensaje de la APP, con el operador ternario ? pregunta si err.error es un string lo muestras, 
          sino (:) muestra el mensaje 'Ha ocurrido un error, intentalo de nuevo.' */
        this.error = typeof err.error === 'string' ? err.error : 'Ha ocurrido un error, intentalo de nuevo.';
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