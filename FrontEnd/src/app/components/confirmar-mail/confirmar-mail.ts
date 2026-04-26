import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-confirmar-mail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmar-mail.html',
  styleUrl: './confirmar-mail.css',
})
export class ConfirmarMail implements OnInit {
  mensaje = '';
  error = '';
  loading = true;

  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.error = 'Token no válido o ausente.';
      this.loading = false;
      return;
    }

    this.authService.altaCliente(token).subscribe({
      next: () => {
        this.mensaje = 'Cuenta confirmada con éxito. Ya puedes iniciar sesión.';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.error = err.error || 'Ha ocurrido un error al confirmar la cuenta.';
        this.loading = false;
      }
    });
  }

}
