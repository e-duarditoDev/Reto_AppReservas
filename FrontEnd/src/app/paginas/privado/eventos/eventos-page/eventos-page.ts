import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TarjetaSercviciosHome } from '../../../../components/tarjeta-sercvicios-home/tarjeta-sercvicios-home';


@Component({
  selector: 'app-eventos-page',
  standalone: true,
  imports: [FormsModule, TarjetaSercviciosHome],
  templateUrl: './eventos-page.html',
  styleUrl: './eventos-page.css',
})
export class EventosPage implements OnInit {

  private http = inject(HttpClient);

  eventos: any[] = [];
  eventosFiltrados: any[] = [];

  busqueda: string = '';
  tipoSeleccionado: string = '';
  soloDestacados: boolean = false;

  ngOnInit() {
    this.http.get<any[]>('http://localhost:8082/eventos')
      .subscribe({
        next: (res) => {
          this.eventos = res;
          this.eventosFiltrados = res;
        },
        error: (err) => {
          console.error('Error al obtener eventos', err);
        }
      });
  }

  filtrar() {
    this.eventosFiltrados = this.eventos.filter(e => {

      const coincideBusqueda =
        e.titulo.toLowerCase().includes(this.busqueda.toLowerCase());

      const coincideTipo =
        !this.tipoSeleccionado || e.tipo === this.tipoSeleccionado;

      const coincideDestacado =
        !this.soloDestacados || e.destacado === true;

      return coincideBusqueda && coincideTipo && coincideDestacado;
    });
  }

  reservar(evento: any) {
    console.log('Reservar evento:', evento);

    // aquí luego llamarás al backend
    // this.http.post('/reservas', {...})
  }
}