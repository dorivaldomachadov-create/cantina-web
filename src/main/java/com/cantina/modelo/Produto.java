package com.cantina.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private int quantidadeEstoque;

    @Column(nullable = false)
    private boolean ativo = true;

    public Produto() {}

    public Integer getId()                  { return id; }
    public String  getNome()               { return nome; }
    public String  getCategoria()          { return categoria; }
    public BigDecimal getPreco()           { return preco; }
    public int     getQuantidadeEstoque()  { return quantidadeEstoque; }
    public boolean isAtivo()               { return ativo; }

    public void setId(Integer id)                      { this.id = id; }
    public void setNome(String nome)                   { this.nome = nome; }
    public void setCategoria(String categoria)         { this.categoria = categoria; }
    public void setPreco(BigDecimal preco)             { this.preco = preco; }
    public void setQuantidadeEstoque(int qtd)          { this.quantidadeEstoque = qtd; }
    public void setAtivo(boolean ativo)                { this.ativo = ativo; }
}
