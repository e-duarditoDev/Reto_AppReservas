import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tarjeta-sercvicios-home', // 
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tarjeta-sercvicios-home.html',
  styleUrl: './tarjeta-sercvicios-home.css',
})
export class TarjetaSercviciosHome {

  // Inputs tipo SIGNAL (Angular moderno)
  icon = input<string>();
  imagen = input<string>();
  titulo = input<string>();
  descripcion = input<string>();

}