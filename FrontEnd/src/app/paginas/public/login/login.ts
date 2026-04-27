import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../servicios/auth-service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {

  email: string = '';
  password: string = '';

  loading: boolean = false;
  error: string = '';

  private authService: any = inject(AuthService); // Inyectamos el servicio de autenticación (simulado)
  private router: any = inject(Router); // Inyectamos el Router para redirigir después del login  
  private cd = inject(ChangeDetectorRef);

  login() {
    this.error = '';

    if (!this.email || !this.password) {
      this.error = 'Todos los campos son obligatorios';
      return;
    }

    this.loading = true;

    this.authService.login(this.email, this.password).subscribe({
      next: (token: string) => {
        localStorage.setItem('jwt', token); // guarda el JWT
        this.router.navigateByUrl('/'); // redirige al home
        this.loading = false;
      },
      error: () => {
        this.error = 'Usuario no registrado o credenciales incorrectas.';
        this.loading = false;
        this.cd.detectChanges();
      }
    });

    // Simulación de login (luego conectamos backend)
    /*     setTimeout(() => {
          if (this.email === 'test@test.com' && this.password === '1234') {
            localStorage.setItem('usuario', this.email);
            console.log('Login correcto');
          } else {
            this.error = 'Credenciales incorrectas';
          }
    
          this.loading = false;
        }, 1000); */

  }
}