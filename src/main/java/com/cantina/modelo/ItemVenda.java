package com.cantina.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    public ItemVenda() {}

    public BigDecimal calcularSubtotal() {
        if (precoUnitario == null) return BigDecimal.ZERO;
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Integer    getId()            { return id; }
    public Venda      getVenda()         { return venda; }
    public Produto    getProduto()       { return produto; }
    public int        getQuantidade()    { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }

    public void setId(Integer id)                    { this.id = id; }
    public void setVenda(Venda venda)                { this.venda = venda; }
    public void setProduto(Produto produto)          { this.produto = produto; }
    public void setQuantidade(int quantidade)        { this.quantidade = quantidade; }
    public void setPrecoUnitario(BigDecimal p)       { this.precoUnitario = p; }
}
