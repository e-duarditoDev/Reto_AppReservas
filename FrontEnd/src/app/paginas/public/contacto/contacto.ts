import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [FormsModule],       // <-- SoloFormsModule
  templateUrl: './contacto.html',
  styleUrl: './contacto.css',
})
export class Contacto {

  nombre = '';
  apellido = '';
  email = '';
  telefono = '';
  mensaje = '';

  constructor(private location: Location) { }

  volver() {
    this.location.back();
  }

  enviar() {
    console.log('Formulario enviado:', this);
  }
}
