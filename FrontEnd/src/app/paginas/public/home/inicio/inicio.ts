import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

//Importar los componentes generados
import { Header } from "../../../../components/header/header";
import { Footer } from "../../../../components/footer/footer";
import { EventoService } from '../../../../servicios/evento-service';
import { Evento } from '../../../../modelo/evento';
import { BodyHome } from '../../../../components/body-home/body-home';

@Component({
  selector: 'app-inicio',
  standalone: true,//Verifica que cada componente secundario tenga tambien standalone en el decorador
  imports: [ //importar directamente aqui y ya aparece arriba
    CommonModule,
    Header,
    BodyHome,
    Footer
  ],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {
  //inyectar el servicio
  private eventoService = inject(EventoService);

  /**
   * propiedad eventos (accesible desde html) 
   * signal es contenedor reactivo, avisa a Angular cuando cambia y se actualiza interfaz(html), hace un set()
   * signal almacena la lista de eventos que se recupere
   * /Evento[] indica el tipo de dato
   * ([]) es el valor inicial al crear el componente (vacio)
   * eventos = signal<Evento[]>([]);
  */
  eventos = signal<Evento[]>([]);
  
  /**
   * Se ejecuta auto cuando se carga la plantilla
   * ngOnInit() metodo de ciclo de vida (lifecycle hook)
   * Angular lo ejecuta al termino de construir el componente
   * getEventos() hace llamada a endPoint que devuelve un Observable
   * Observable es lazy, la llamada no se realiza hasta que alguien se suscribe
   * suscribe hace llamada y avisa con la respuesta
   * si ok camino next(datos), datos es variable donde se guarda lista
   * this.eventos.set(datos) inyectan datos dentro del signal eventos
   * eventos es dibujado en el html
   * si error, muestra el error del back
   */
  ngOnInit(): void {
    this.eventoService.getEventos().subscribe({
      next: (datos) => this.eventos.set(datos),
      error: (error) => console.error('Error al obtener eventos:', error),
    });
  }

 }
