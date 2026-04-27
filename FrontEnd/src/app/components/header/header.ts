import { Component, inject, input, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  variante = input<'publica' | 'privada' | 'admon' | 'contacto'>('publica');

  infoUsuario = signal<string>('');

  private authService = inject(AuthService);
  private router = inject(Router);

  get cliente(): boolean {
    return this.authService.getRol() === 'ROLE_CLIENTE';
  }

  get admon(): boolean {
    return this.authService.getRol() === 'ROLE_ADMIN';
  }

  ngOnInit(): void {
    if (this.variante() === 'privada' || this.variante() === 'admon') {
      const token = localStorage.getItem('jwt');
      if (!token) return;
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const nombre: string = payload.nombre ?? payload.name ?? '';
        const sub: string = payload.sub ?? '';
        this.infoUsuario.set(nombre && nombre !== sub ? `${nombre} | ${sub}` : sub);
      } catch { /* token inválido */ }
    }
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
