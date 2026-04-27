import { Routes } from '@angular/router';
import { Inicio } from './paginas/public/home/inicio/inicio';
import { Login } from './paginas/public/login/login';
import { Registro } from './paginas/public/registro/registro';
import { RecuperarPassword } from './paginas/public/recuperar-password/recuperar-password'; 

/* Asocia una ruta (path) con un Componente (mapa de rutas) */
export const routes: Routes = [
  {
    path: '',
    component: Inicio,
    title: 'Página de inicio'
  },
  {
    path: 'login',
    loadComponent: () => import('./paginas/public/login/login').then(comp => comp.Login),
    title: 'Iniciar sesión'
  },
  {
    path: 'registro',
    loadComponent: () => import('./paginas/public/registro/registro').then(comp => comp.Registro),
    title: 'Registro'
  },
  {
    path: 'recuperar-password',
    loadComponent: () => import('./paginas/public/recuperar-password/recuperar-password').then(comp => comp.RecuperarPassword),
    title: 'Recuperar contraseña'
  },
    {
      path: 'contacto',   
      loadComponent: () => import('./paginas/public/contacto/contacto').then(comp => comp.Contacto),
      title: 'Recuperar contraseña'
  },

  {
    path: 'confirmar-mail',
    loadComponent: () => import('./components/confirmar-mail/confirmar-mail').then(comp => comp.ConfirmarMail),
    title: 'Confirmar correo electrónico'
  },

  {
    path: 'completar-usuario',
    loadComponent: () => import('./components/completar-usuario/completar-usuario')
    .then(comp => comp.CompletarUsuario),
    title: 'Completar perfil de usuario'
  },

    {
    path: '**', // ruta wildcard, se activa si no se encuentra ninguna ruta coincidente
    redirectTo: '' // Redirige a la ruta raíz (home) si no se encuentra la ruta solicitada
  },

];