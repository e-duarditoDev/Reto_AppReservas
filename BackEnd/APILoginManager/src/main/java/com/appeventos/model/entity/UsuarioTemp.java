package com.appeventos.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "usuario_temp")
public class UsuarioTemp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true, length = 100)
	private String email;
	@Column(nullable = false, length = 70)
	private String password;
	@Column(name = "alias_usuario", nullable = false, length = 50)
	private String aliasUsuario;
	@Column(nullable = false, unique = true, length = 50)
	private String token;
	@Column(name = "fecha_expiracion", nullable = false)
	private LocalDateTime fechaExpiracion;
}
