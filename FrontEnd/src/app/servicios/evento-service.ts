import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento } from '../modelo/evento';


@Injectable({
  providedIn: 'root',
})
export class EventoService {
  
  //solo puede ser usada dentro de la clase (private) no puede ser reasignada (readonly)
  //inject instancia la clase (inyecta dependencias), HttpClient permite hacer peticiones HTTP
  private readonly http = inject(HttpClient);
  //URL apunta a la api
  private readonly apiUrl = 'http://localhost:8080/eventos'

  //Obtiene la lista completa de eventos del backend
  getEventos(): Observable<Evento[]> {
    return this.http.get<Evento[]>(this.apiUrl);
  }

  //Obtiene el evento por id
  getEventoId(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.apiUrl}/${id}`);
  }
}
