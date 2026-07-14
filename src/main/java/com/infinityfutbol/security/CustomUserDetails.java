package com.infinityfutbol.security;

import com.infinityfutbol.entity.enums.EstadoUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String idUsuario;
    private final String username;
    private final String password;
    private final EstadoUsuario estado;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            String idUsuario,
            String username,
            String password,
            EstadoUsuario estado,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.estado = estado;
        this.authorities = authorities;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado == EstadoUsuario.ACTIVO;
    }
}