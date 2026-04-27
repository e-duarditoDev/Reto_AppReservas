import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './footer.html',
  styleUrl: './footer.css',
})
export class Footer {
    variante = input<'publica' | 'privada' | 'admon' | 'contacto'>('publica'); // (publica) es el valor por defecto
}
