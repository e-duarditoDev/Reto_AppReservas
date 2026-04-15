package com.appeventos.model.entity;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="usuario")
public class Usuario implements Serializable, UserDetails{

	private static final long serialVersionUID = 1L;//Seriablizable solo si se guarda en sesion y para Id compuestas

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Long idUsuario; 
	
	@Column(nullable = false, name = "alias_usuario", unique = true, length = 50)
	//no puede llamarse userName porque el getter se construye con el username del userDetails
	private String aliasUsuario; 
	
	@Column(nullable = false, length = 70)
	private String password;
	
	@Column(nullable = false, unique = true, length = 100)
	private String email; 
	
	@Column(nullable = false, name = "fecha_registro")
	private LocalDate fechaRegistro; 

	@Column(length = 30)
	private String nombre;
	
	@Column(name="primer_apellido", length = 45)
	private String primerApellido;
	
	@Column(name="segundo_apellido", length = 45)
	private String segundoApellido;
	
	@Column(length = 100)
	private String direccion; 
	
	@Column(length = 1)
	private Integer enabled; 
	
	//CascadeType.All si un usuario se borra, se borra el registro 
	//hijo en la tabla usuraio_perfiles
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private  List<UsuarioPerfiles> perfiles;

	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		if(perfiles == null)
			return Collections.emptyList();
		
        return perfiles.stream()//convierte lista en flujo de elementos, recorre elementos
                .map(usuarioPerfil -> usuarioPerfil.getPerfil().getNombre())//de cada usuarioPerfil obtiene el perfil.nombre (ROLE_CLIENTE)
                .map(SimpleGrantedAuthority::new)//cada perfil.nombre se convierte en SimpleGrantedAuthority
                .toList();//convierte el strem en una lista
	}

	//password lo interpreta del getter de lombok, al llamarse password

	//sin embargo para userName, no obliga aunque tengas un atributo userName, Spring pregunta
	//que atributo es userName.
	@Override
	public String getUsername() {//
		return email;
	}
}
