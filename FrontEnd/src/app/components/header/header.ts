import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  variante = input<'publica' | 'privada' | 'admon' | 'contacto'>('publica'); // (publica) es el valor por defecto
}
