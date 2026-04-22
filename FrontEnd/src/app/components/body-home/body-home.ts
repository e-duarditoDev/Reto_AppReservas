import { Component } from '@angular/core';
import { TarjetaSercviciosHome } from '../tarjeta-sercvicios-home/tarjeta-sercvicios-home';

@Component({
  selector: 'app-body-home',
  standalone: true,
  imports: [
    TarjetaSercviciosHome
  ],
  templateUrl: './body-home.html',
  styleUrl: './body-home.css',
})
export class BodyHome {}
