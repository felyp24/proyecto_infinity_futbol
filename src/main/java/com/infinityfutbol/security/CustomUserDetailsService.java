package com.infinityfutbol.security;

import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public CustomUserDetailsService(
            UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        List<SimpleGrantedAuthority> authorities =
                usuarioRolRepository
                        .findByUsuario_IdUsuario(usuario.getIdUsuario())
                        .stream()
                        .filter(asignacion ->
                                Boolean.TRUE.equals(
                                        asignacion.getRol().getEstado()
                                )
                        )
                        .map(asignacion ->
                                new SimpleGrantedAuthority(
                                        "ROLE_"
                                                + asignacion
                                                .getRol()
                                                .getNombreRol()
                                )
                        )
                        .distinct()
                        .toList();

        return new CustomUserDetails(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEstado(),
                authorities
        );
    }
}