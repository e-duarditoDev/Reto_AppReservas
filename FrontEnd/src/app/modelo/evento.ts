import { EstadoEvento } from "./estadoEventoType";
import { EventoDestacado } from "./eventoDestacado";

//el modelo debe conicidir con los DTO de la API
export interface Evento {
    idEvento: number;
    nombre: string;
    descripcion: string;
    fechaInicio: string;//de la API viene un JSON string
    duracion: number;
    direccion: string;
    estado: EstadoEvento;//enums
    destacado: EventoDestacado;//enums
    aforo: number;
    precio: number;
    idTipoEvento: number;
}
