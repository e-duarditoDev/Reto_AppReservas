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

    // @MapsId("aliasUsuario") indica que esta FK forma parte del EmbeddedId,
    // concretamente del campo "aliasUsuario" en UsuarioPerfilesId.
    // El string debe ser el NOMBRE DEL CAMPO JAVA en la clase @Embeddable,
    // no el nombre de la columna SQL.
    @MapsId("aliasUsuario")
    @ManyToOne(optional = false)
    @JoinColumn(name = "username", nullable = false)
    private Usuario usuario;

    @MapsId("idPerfil")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;
}
