import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoEvento } from '../modelo/tipo-evento';

@Injectable({
  providedIn: 'root',
})
export class TipoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/tipos';

  getTipos(): Observable<TipoEvento[]> {
    return this.http.get<TipoEvento[]>(this.apiUrl);
  }
}
