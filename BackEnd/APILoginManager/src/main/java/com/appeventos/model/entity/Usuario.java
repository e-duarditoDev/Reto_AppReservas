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
public class Usuario implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    // PK: alias del usuario (parte del email antes de @).
    // El campo se llama aliasUsuario para evitar conflicto con getUsername() de UserDetails
    // que Lombok tambien intentaria generar si el campo se llamara username.
    @Id
    @Column(name = "username", nullable = false, unique = true, length = 45)
    private String aliasUsuario;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(length = 30)
    private String nombre;

    @Column(length = 45)
    private String apellidos;

    @Column(length = 100)
    private String direccion;

    @Column(length = 1)
    private Integer enabled;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<UsuarioPerfiles> perfiles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (perfiles == null)
            return Collections.emptyList();

        return perfiles.stream()
                .map(up -> up.getPerfil().getNombre())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    // Devuelve aliasUsuario como identificador de Spring Security y subject del JWT
    @Override
    public String getUsername() {
        return aliasUsuario;
    }
}
