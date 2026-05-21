package com.cantina.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relacionamento com a Venda (Muitos itens para uma Venda)
    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    // Relacionamento com o Produto (Muitos itens podem referenciar o mesmo Produto)
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    private int quantidade;

    @Column(name = "preco_unitario")
    private double precoUnitario; // Guarda o preço do momento da venda (histórico)

    // Construtor padrão exigido pelo JPA
    public ItemVenda() {
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }
    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}