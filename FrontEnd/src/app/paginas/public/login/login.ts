import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  email: string = '';
  password: string = '';

  loading: boolean = false;
  error: string = '';

  login() {
    this.error = '';

    if (!this.email || !this.password) {
      this.error = 'Todos los campos son obligatorios';
      return;
    }

    this.loading = true;

    // Simulación de login (luego conectamos backend)
    setTimeout(() => {
      if (this.email === 'test@test.com' && this.password === '1234') {
        localStorage.setItem('usuario', this.email);
        console.log('Login correcto');
      } else {
        this.error = 'Credenciales incorrectas';
      }

      this.loading = false;
    }, 1000);
  }
}