package com.cantina.servico;

import com.cantina.repositorio.UtilizadorRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecuperacaoServico {

    private final UtilizadorRepositorio repo;
    private final PasswordEncoder encoder;

    public RecuperacaoServico(UtilizadorRepositorio repo, PasswordEncoder encoder) {
        this.repo    = repo;
        this.encoder = encoder;
    }

    @Transactional
    public String gerarToken(String email) {
        var opt = repo.findByEmail(email);
        if (opt.isEmpty()) return null;
        var u = opt.get();
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        u.setTokenRecuperacao(token);
        u.setTokenExpiracao(LocalDateTime.now().plusMinutes(30));
        repo.save(u);
        return token;
    }

    public boolean tokenValido(String token) {
        return repo.findByTokenRecuperacao(token)
            .map(u -> u.getTokenExpiracao() != null && LocalDateTime.now().isBefore(u.getTokenExpiracao()))
            .orElse(false);
    }

    @Transactional
    public boolean redefinir(String token, String nova) {
        if (!tokenValido(token)) return false;
        return repo.findByTokenRecuperacao(token).map(u -> {
            u.setPasswordHash(encoder.encode(nova));
            u.setTokenRecuperacao(null);
            u.setTokenExpiracao(null);
            repo.save(u);
            return true;
        }).orElse(false);
    }
}
