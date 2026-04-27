import { Routes } from '@angular/router';
import { Inicio } from './paginas/public/home/inicio/inicio';

export const routes: Routes = [
  {
    path: '',
    component: Inicio,
    title: 'Página de inicio'
  },
  {
    path: 'login',
    loadComponent: () => import('./paginas/public/login/login').then(m => m.Login),
    title: 'Iniciar sesión'
  },
  {
    path: 'registro',
    loadComponent: () => import('./paginas/public/registro/registro').then(m => m.Registro),
    title: 'Registro'
  },
  {
    path: 'recuperar-password',
    loadComponent: () => import('./paginas/public/recuperar-password/recuperar-password')
      .then(m => m.RecuperarPassword),
    title: 'Recuperar contraseña'
  },
  {
    path: 'contacto',
    loadComponent: () => import('./paginas/public/contacto/contacto').then(m => m.Contacto),
    title: 'Contacto'
  },
  {
    path: 'confirmar-mail',
    loadComponent: () => import('./components/confirmar-mail/confirmar-mail').then(m => m.ConfirmarMail),
    title: 'Confirmar correo'
  },
  {
    path: 'completar-usuario',
    loadComponent: () => import('./components/completar-usuario/completar-usuario')
      .then(m => m.CompletarUsuario),
    title: 'Completar usuario'
  },
  {
    path: '**',
    redirectTo: ''
  }
];