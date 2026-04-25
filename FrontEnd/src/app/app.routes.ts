import { Routes } from '@angular/router';
import { Inicio } from './paginas/public/home/inicio/inicio';
import { Login } from './paginas/public/login/login';
import { Registro } from './paginas/public/registro/registro';
import { RecuperarPassword } from './paginas/public/recuperar-password/recuperar-password';

/* Asocia una ruta (path) con un Componente (mapa de rutas) */
export const routes: Routes = [
  {
    path: '', // Ruta RAIZ (home), se referencia con routerlink="" en el html
    component: Inicio,
    title: 'Página de inicio' // titulo de la pestana del navegador, UX y SEO
  },
  {
    path: 'login',
    // component: Login, // Carga directa sin lazy loading
    loadComponent: () => import('./paginas/public/login/login').then(comp => comp.Login), //carga lazy loading del componente, se carga solo cuando se accede a la ruta
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
      path: 'contacto',   loadComponent: () => import('./paginas/public/contacto/contacto').then(comp => comp.Contacto),
    title: 'Recuperar contraseña'
  },
  {
    path: '**', // ruta wildcard, se activa si no se encuentra ninguna ruta coincidente
    redirectTo: '' // Redirige a la ruta raíz (home) si no se encuentra la ruta solicitada
  }
];