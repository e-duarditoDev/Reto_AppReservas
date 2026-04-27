package com.appeventos.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UsuarioRegistroDto {

	private String nombre;
	private String apellidos;
	private String direccion;
	
}

