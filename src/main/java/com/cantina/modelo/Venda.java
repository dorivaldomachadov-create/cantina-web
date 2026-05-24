package com.cantina.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "valor_total")
    private double valorTotal;

    @Column(name = "nome_cliente")
    private String nomeCliente;

    // Campo para armazenar o estado da venda (ABERTA, FECHADA, CANCELADA)
    @Column(name = "estado")
    private String estado = "FECHADA";

    // O CascadeType.ALL garante que ao salvar a Venda, o JPA salva todos os seus itens automaticamente
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    // Construtor padrão exigido pelo JPA
    public Venda() {
    }

    public double calcularTotal() {
        if (this.itens == null || this.itens.isEmpty()) {
            return this.valorTotal;
        }
        return this.itens.stream()
                .mapToDouble(ItemVenda::calcularSubtotal)
                .sum();
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public double getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    // Getter e Setter para o Thymeleaf conseguir ler o estado
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }
    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
}
