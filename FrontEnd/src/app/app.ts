import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Inicio } from './paginas/public/home/inicio/inicio';

@Component({
  selector: 'app-root',
  standalone: true,//verifica los subcomponentes son standalone
  imports: [
    RouterOutlet,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('FrontEnd');
}
