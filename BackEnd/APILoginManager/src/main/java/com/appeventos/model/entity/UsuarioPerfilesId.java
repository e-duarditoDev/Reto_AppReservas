package com.appeventos.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class UsuarioPerfilesId implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "username")
	private String aliasUsuario;
	@Column(name = "id_perfil")
	private Long idPerfil;

}
