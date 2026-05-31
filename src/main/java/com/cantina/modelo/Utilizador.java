package com.cantina.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "utilizadores", indexes = {
    @Index(name = "idx_utilizador_username", columnList = "username", unique = true),
    @Index(name = "idx_utilizador_email",    columnList = "email",    unique = true)
})
public class Utilizador {

    public enum Perfil { GERENTE, FUNCIONARIO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(unique = true)
    private String email;

    private String telefone;
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "token_recuperacao")
    private String tokenRecuperacao;

    @Column(name = "token_expiracao")
    private LocalDateTime tokenExpiracao;

    public Utilizador() {}

    public Utilizador(String nome, String username, String passwordHash,
                      String email, String telefone, String cargo, Perfil perfil) {
        this.nome         = nome;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.email        = email;
        this.telefone     = telefone;
        this.cargo        = cargo;
        this.perfil       = perfil;
        this.ativo        = true;
        this.dataCriacao  = LocalDateTime.now();
    }

    public String getAvatarInicial() {
        return (nome != null && !nome.isEmpty())
            ? String.valueOf(nome.charAt(0)).toUpperCase() : "U";
    }

    public String getDataCriacaoFormatada() {
        if (dataCriacao == null) return "—";
        return dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public boolean isGerente() { return perfil == Perfil.GERENTE; }

    public Integer       getId()                { return id; }
    public String        getNome()              { return nome; }
    public String        getUsername()          { return username; }
    public String        getPasswordHash()      { return passwordHash; }
    public String        getEmail()             { return email; }
    public String        getTelefone()          { return telefone; }
    public String        getCargo()             { return cargo; }
    public Perfil        getPerfil()            { return perfil; }
    public boolean       isAtivo()              { return ativo; }
    public LocalDateTime getDataCriacao()       { return dataCriacao; }
    public String        getTokenRecuperacao()  { return tokenRecuperacao; }
    public LocalDateTime getTokenExpiracao()    { return tokenExpiracao; }

    public void setId(Integer id)                        { this.id = id; }
    public void setNome(String nome)                     { this.nome = nome; }
    public void setUsername(String u)                    { this.username = u; }
    public void setPasswordHash(String p)                { this.passwordHash = p; }
    public void setEmail(String e)                       { this.email = e; }
    public void setTelefone(String t)                    { this.telefone = t; }
    public void setCargo(String c)                       { this.cargo = c; }
    public void setPerfil(Perfil p)                      { this.perfil = p; }
    public void setAtivo(boolean a)                      { this.ativo = a; }
    public void setDataCriacao(LocalDateTime d)          { this.dataCriacao = d; }
    public void setTokenRecuperacao(String t)            { this.tokenRecuperacao = t; }
    public void setTokenExpiracao(LocalDateTime t)       { this.tokenExpiracao = t; }
}
