import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  //constructor(private http: HttpClient) {}

  private http = inject(HttpClient);
  private llamadaApi = 'http://localhost:8082/auth';

  // Paso 1: Envía email + password → genera usuarioTemp y manda el correo de confirmación
  confirmarEmail(email: string, password: string): Observable<string> {
    return this.http.post(
      `${this.llamadaApi}/confirmar-email`,
      { email, password }, //DTO serializado como JSON
      { responseType: 'text' }
    );
  }

  // Paso 2: Se llama desde el link del email, crea el usuario definitivo
  altaCliente(
    tokenTemp: string,
    datos: { nombre: string; apellidos: string; direccion: string }): 
    Observable<string> {
    return this.http.post(
      `${this.llamadaApi}/alta-cliente/${tokenTemp}`,datos, //DTO serializado como JSON
      { responseType: 'text' }
    );
  }

  // Login: devuelve el JWT como string plano
  login(email: string, password: string): Observable<string> {
    return this.http.post(
      `${this.llamadaApi}/login`,
      { email, password }, //DTO serializado como JSON
      { responseType: 'text' }
    );
  } 

}

