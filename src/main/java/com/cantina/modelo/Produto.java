package com.cantina.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "cantina")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String categoria;
    private double preco;

    @Column(name = "quantidade_estoque")
    private int quantidadeEstoque;

    // Campo para controlar se o produto está ativo ou "apagado"
    @Column(name = "ativo")
    private boolean ativo = true;

    // Getters e Setters existentes...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    // Getter e Setter para a propriedade ativo
    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}