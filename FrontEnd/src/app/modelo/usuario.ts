import { Perfil } from "./perfil";

export interface Usuario {
    userName: string;
    nombre: string;
    apellidos: string;
    direccion: string;
    enabled: boolean;
    fechaRegistro: string;//de la API viene JSON string
    perfiles: Perfil[]; //relacion N/M con perfil
}
