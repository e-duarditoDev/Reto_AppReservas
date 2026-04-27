package com.appeventos.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false, length = 45)
    private String apellidos;

    @Column(nullable = true, length = 100)
    private String direccion;

    @Column(nullable = false, length = 1)
    private Integer enabled;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<UsuarioPerfiles> usuarioPerfil;
}
