package com.appeventos.model.entity;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table (name = "usuario_perfiles")
public class UsuarioPerfiles implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//Representa la id de la entidad cuando la PK es compuesta en una entidad independiente
	@EmbeddedId
	private UsuarioPerfilesId idUsuarioPerfiles;

	//meter aqui insertable = false y updatable = false
    //porque el valor ya se escribe a través de UsuarioPerfilesId.userName
	//con referencedColumnName la FK referencia a alias_usurio de Usuario
	@ManyToOne(optional = false)
	@JoinColumn(
	name = "alias_usuario", 
	referencedColumnName = "alias_usuario", 
	insertable = false, 
	updatable = false)
	private Usuario usuario;
	
	@MapsId("idPerfil")//Aqui si, porque UsuarioPerfiles usa @Id de Perfil
	@ManyToOne(optional = false)
	@JoinColumn(name = "id_perfil")
	private Perfil perfil;
}
