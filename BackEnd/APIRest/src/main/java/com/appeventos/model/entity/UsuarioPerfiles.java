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
@Table(name = "usuario_perfiles")
public class UsuarioPerfiles implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UsuarioPerfilesId idUsuarioPerfiles;

    // "username" es el nombre del campo Java en UsuarioPerfilesId
    @MapsId("username")
    @ManyToOne(optional = false)
    @JoinColumn(name = "username", nullable = false)
    private Usuario usuario;

    // "idPerfil" es el nombre del campo Java en UsuarioPerfilesId (no el nombre de columna "id_perfil")
    @MapsId("idPerfil")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;
}
