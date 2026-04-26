import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../servicios/auth-service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  email: string = '';
  password: string = '';

  loading: boolean = false;
  error: string = '';

  private authService = inject(AuthService);
  private router = inject(Router);
  private location: Location = inject(Location);


  volver() {
    this.location.back();
  }

  login() {
    this.error = '';

    if (!this.email || !this.password) {
      this.error = 'Todos los campos son obligatorios';
      return;
    }

    this.loading = true;

    this.authService.login(this.email, this.password).subscribe({
      next: (res: any) => {
        const token = res.token || res;

        localStorage.setItem('jwt', token);
        this.router.navigate(['/']);

        this.loading = false;
      },
      error: () => {
        this.error = 'Usuario no registrado o credenciales incorrectas.';
        this.loading = false;
      }
    });
  }
}