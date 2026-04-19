import { Routes } from '@angular/router';
import { Inicio } from './paginas/public/home/inicio/inicio';

export const routes: Routes = [
    //Hacer visible un componente 
    {
        path: '',
        component: Inicio,
        title: 'Pagina de inicio o home.'
    },
    {
        path: '',
        redirectTo: ''
    }



];
