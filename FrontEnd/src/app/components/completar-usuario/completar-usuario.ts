import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-completar-usuario',
  imports: [CommonModule, FormsModule],
  templateUrl: './completar-usuario.html',
  styleUrl: './completar-usuario.css',
})
export class CompletarUsuario implements OnInit {
  nombre = '';
  apellidos = '';
  direccion = '';

  error = '';
  errorLocal = '';
  mensajeOk = '';
  loading = false;

  private token = '';
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  private cd = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    if (!this.token) {
      this.error = 'Token no válido o ausente.';
    }
  }

  
  completarRegistro(): void {
    this.errorLocal = ''; // validación de campos vacíos
    this.error = '';

/*     if (!this.nombre || !this.apellidos || !this.direccion) {
      this.errorLocal = 'Todos los campos son obligatorios.';
      return;
    } */

    this.loading = true;

    this.authService.altaCliente(this.token, {
      nombre: this.nombre,
      apellidos: this.apellidos,
      direccion: this.direccion
    }).subscribe({
      next: () => {
        this.mensajeOk = 'Cuenta creada con exito. Redirigiendo al login...';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.error = err.error || 'Ha ocurrido un error al completar el registro.';
        this.loading = false;
        this.cd.detectChanges();
      },
    });
  }
}
