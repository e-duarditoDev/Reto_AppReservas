package com.appeventos.model.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name="perfiles")
public class Perfil implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_perfil")
	private Long idPerfil;
	
	@Column(nullable = false)
	private String nombre;
	
	//CascadeType.All si un usuario se borra, se borra el registro 
	//hijo en la tabla usuraio_perfiles
	@OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL)
	private List<UsuarioPerfiles> perfilUsuario;
	
}
