package com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.repository.UsuarioRepository;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = _getTokenFromRequest(request);

        if (Objects.nonNull(token)) {
            String subject = tokenService.getSubject(token);

            UserDetails usuario = usuarioRepository.findByEmail(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                    usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String _getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (Objects.nonNull(authHeader)) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

}
