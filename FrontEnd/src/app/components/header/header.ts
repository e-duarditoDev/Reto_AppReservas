import { Component, input, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  variante = input<'publica' | 'privada' | 'admon' | 'contacto'>('publica'); // (publica) es el valor por defecto

  private authService = inject(AuthService);

  get estaAutenticado(): boolean {
    return this.authService.isLoggedIn();
  }

  get cliente(): boolean {
    return this.authService.getRol() === 'ROLE_CLIENTE';
  }

  get admon(): boolean {
    return this.authService.getRol() === 'ROLE_ADMIN';
  }

  logout(): void {
    this.authService.logout();
  }



}
