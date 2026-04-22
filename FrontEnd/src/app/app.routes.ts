import { Routes } from '@angular/router';
import { Inicio } from './paginas/public/home/inicio/inicio';
import { Login } from './paginas/public/login/login';
import { Registro } from './paginas/public/registro/registro';
import { RecuperarPassword } from './paginas/public/recuperar-password/recuperar-password';

export const routes: Routes = [
  {
    path: '',
    component: Inicio,
    title: 'Página de inicio'
  },
  {
    path: 'login',
    component: Login,
    title: 'Iniciar sesión'
  },
  {
    path: 'registro',
    component: Registro,
    title: 'Registro'
  },
  {
    path: 'recuperar-password',
    component: RecuperarPassword
  },
  {
    path: '**',
    redirectTo: ''
  }
];