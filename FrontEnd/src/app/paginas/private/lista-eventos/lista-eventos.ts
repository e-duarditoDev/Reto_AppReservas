import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Header } from '../../../components/header/header';
import { EventoService } from '../../../servicios/evento-service';
import { ReservaService } from '../../../servicios/reserva-service';
import { TipoService } from '../../../servicios/tipo-service';
import { Evento } from '../../../modelo/evento';
import { TipoEvento } from '../../../modelo/tipo-evento';

@Component({
  selector: 'app-lista-eventos',
  standalone: true,
  imports: [CommonModule, FormsModule, Header],
  templateUrl: './lista-eventos.html',
  styleUrl: './lista-eventos.css',
})
export class ListaEventos {
  private eventoService = inject(EventoService);
  private reservaService = inject(ReservaService);
  private tipoService = inject(TipoService);
  private router = inject(Router);
  private cd = inject(ChangeDetectorRef);

  eventos = signal<Evento[]>([]);
  tipos = signal<TipoEvento[]>([]);

  filtroEstado = '';
  filtroTipo = '';

  eventoSeleccionado: Evento | null = null;
  cantidad = 1;
  observaciones = '';
  cargandoReserva = false;
  reservaExito = false;
  reservaError = '';

  private readonly colores = ['#FF9800', '#E91E63', '#2196F3', '#4CAF50', '#9C27B0', '#FF5722'];

  get eventosFiltrados(): Evento[] {
    return this.eventos().filter(e => {
      const okEstado = !this.filtroEstado || e.estado === this.filtroEstado;
      const okTipo = !this.filtroTipo || e.idTipoEvento === Number(this.filtroTipo);
      return okEstado && okTipo;
    });
  }

  ngOnInit(): void {
    this.eventoService.getEventos().subscribe({
      next: (datos) => this.eventos.set(datos),
      error: (err) => console.error('Error al cargar eventos:', err),
    });
    this.tipoService.getTipos().subscribe({
      next: (datos) => this.tipos.set(datos),
      error: (err) => console.error('Error al cargar tipos:', err),
    });
  }

  getTipoNombre(idTipo: number): string {
    return this.tipos().find(t => t.idTipo === idTipo)?.nombre ?? `Tipo ${idTipo}`;
  }

  getColorTipo(idTipo: number): string {
    return this.colores[(idTipo - 1) % this.colores.length];
  }

  abrirModal(evento: Evento): void {
    this.eventoSeleccionado = evento;
    this.cantidad = 1;
    this.observaciones = '';
    this.reservaExito = false;
    this.reservaError = '';
  }

  cerrarModal(): void {
    this.eventoSeleccionado = null;
  }

  confirmarReserva(): void {
    if (!this.eventoSeleccionado) return;
    this.cargandoReserva = true;
    this.reservaError = '';
    this.reservaService.crearReserva(this.eventoSeleccionado.idEvento).subscribe({
      next: () => {
        this.reservaExito = true;
        this.cargandoReserva = false;
        this.cd.detectChanges();
      },
      error: () => {
        this.reservaError = 'No se pudo realizar la reserva. Inténtelo de nuevo.';
        this.cargandoReserva = false;
        this.cd.detectChanges();
      },
    });
  }

  volver(): void {
    this.router.navigateByUrl('/');
  }
}
