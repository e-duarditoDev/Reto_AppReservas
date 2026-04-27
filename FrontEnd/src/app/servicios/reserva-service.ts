import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva } from '../modelo/reserva';

@Injectable({
  providedIn: 'root',
})
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/reservas';

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt') ?? '';
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  crearReserva(idEvento: number): Observable<Reserva> {
    return this.http.post<Reserva>(
      `${this.apiUrl}/${idEvento}`,
      null,
      { headers: this.getHeaders() }
    );
  }
}
