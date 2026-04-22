import { Component, input } from '@angular/core';

@Component({
  selector: 'app-tarjeta-sercvicios-home',
  standalone: true,
  imports: [],
  templateUrl: './tarjeta-sercvicios-home.html',
  styleUrl: './tarjeta-sercvicios-home.css',
})
export class TarjetaSercviciosHome {

  //para que se pueda reutilizar la caja tarjeta el componente debe aceptar datos
  //al ser required angular dara un error si falta en el html
  icon = input.required<string>();//coge de public/
  imagen = input.required<string>();
  titulo = input.required<string>();//a definir en el html de inicio
  descripcion = input.required<string>();//a definir en el html de inicio

}
