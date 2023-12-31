package com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.dto.AutenticacaoDTO;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.dto.UsuarioDTO;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.model.PerfilUsuario;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.model.Usuario;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.repository.UsuarioRepository;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.service.AuthenticationService;
import com.gerenciamento.tarefas.rest.api.gerenciamentotarefasrestapi.service.TokenService;


import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AutenticacaoController {

    @PostMapping("/v1/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody @Valid AutenticacaoDTO autenticacaoDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            autenticacaoDTO.getEmail(),
                            autenticacaoDTO.getSenha()));

            Usuario loggedUser = (Usuario) authentication.getPrincipal();

            String token = tokenService.generateToken(loggedUser);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UsuarioDTO(loggedUser.getNome(),
                            loggedUser.getSobrenome(),
                            loggedUser.getEmail(),
                            token,
                            loggedUser.getPerfilUsuario()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/v1/cadastro")
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody @Valid Usuario usuario) {
        try {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            String senha = usuario.getSenha();

            BCryptPasswordEncoder encoder = authenticationService.getPasswordEncoder();

            usuario.setSenha(encoder.encode(senha));
            usuario.setPerfilUsuario(new PerfilUsuario(2));

            usuario = usuarioRepository.save(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new UsuarioDTO(usuario.getNome(),
                            usuario.getSobrenome(),
                            usuario.getEmail(),
                            null,
                            usuario.getPerfilUsuario()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    

  

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

}
