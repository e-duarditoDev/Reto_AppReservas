package com.appeventos.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class UsuarioPerfilesId implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String username;
	@Column(name = "id_perfil")
	private Long idPerfil;

}
