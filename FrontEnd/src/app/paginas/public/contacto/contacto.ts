import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';
import { Header } from '../../../components/header/header';
import { Footer } from "../../../components/footer/footer";
import { RouterLink, RouterModule } from "@angular/router";

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [FormsModule, Header, Footer, RouterLink, RouterModule],       // <-- SoloFormsModule
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
