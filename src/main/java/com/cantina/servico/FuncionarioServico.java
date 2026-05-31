package com.cantina.servico;

import com.cantina.modelo.Utilizador;
import com.cantina.repositorio.UtilizadorRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioServico {

    private final UtilizadorRepositorio repo;
    private final PasswordEncoder encoder;

    public FuncionarioServico(UtilizadorRepositorio repo, PasswordEncoder encoder) {
        this.repo    = repo;
        this.encoder = encoder;
    }

    public List<Utilizador> listarTodos() { return repo.findAll(); }

    public Utilizador buscarPorId(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public String criar(String nome, String username, String password,
                    String email, String telefone, String cargo,
                    Utilizador.Perfil perfil) {

    if (nome == null || nome.isBlank())
        return "Nome é obrigatório.";
    if (!nome.matches("[A-Za-zÀ-ÖØ-öø-ÿ\\s]+"))
        return "O nome só pode conter letras e espaços.";

    if (telefone != null && !telefone.isBlank()) {
        String tel = telefone.replaceAll("\\s", "");
        if (!tel.matches("\\+244[9][0-9]{8}"))
            return "Telefone inválido. Use o formato: +244 9XXXXXXXX";
    }

    if (repo.existsByUsername(username)) return "Nome de utilizador já existe.";
    if (email != null && !email.isBlank() && repo.existsByEmail(email)) return "Email já registado.";
    if (password == null || password.length() < 4) return "Password deve ter pelo menos 4 caracteres.";

    repo.save(new Utilizador(nome, username, encoder.encode(password),
        email, telefone, cargo == null || cargo.isBlank() ? "Operador de Caixa" : cargo, perfil));
    return null;
}

    public String atualizar(Integer id, String nome, String email, String telefone,
                             String cargo, Utilizador.Perfil perfil) {
        var u = repo.findById(id).orElse(null);
        if (u == null) return "Utilizador não encontrado.";
        u.setNome(nome); u.setEmail(email); u.setTelefone(telefone);
        u.setCargo(cargo); u.setPerfil(perfil);
        repo.save(u);
        return null;
    }

    public String alterarPassword(Integer id, String nova) {
        if (nova == null || nova.length() < 4) return "Password deve ter pelo menos 4 caracteres.";
        var u = repo.findById(id).orElse(null);
        if (u == null) return "Utilizador não encontrado.";
        u.setPasswordHash(encoder.encode(nova));
        repo.save(u);
        return null;
    }

    public void toggleAtivo(Integer id) {
        repo.findById(id).ifPresent(u -> { u.setAtivo(!u.isAtivo()); repo.save(u); });
    }
}
