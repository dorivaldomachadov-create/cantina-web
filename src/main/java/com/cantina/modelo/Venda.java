package com.cantina.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas", indexes = {
    @Index(name = "idx_venda_estado",    columnList = "estado"),
    @Index(name = "idx_venda_data_hora", columnList = "data_hora")
})
public class Venda {

    public enum Estado { ABERTA, FECHADA, CANCELADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "nome_cliente")
    private String nomeCliente;

    @Column(name = "nome_funcionario")
    private String nomeFuncionario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ABERTA;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    public Venda() {}

    public BigDecimal calcularTotal() {
        if (itens == null || itens.isEmpty()) return BigDecimal.ZERO;
        return itens.stream()
            .map(ItemVenda::calcularSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getDataHoraFormatada() {
        if (dataHora == null) return "—";
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getNumeroFormatado() {
        return String.format("#%05d", id != null ? id : 0);
    }

    public boolean isAberta()    { return Estado.ABERTA.equals(estado); }
    public boolean isFechada()   { return Estado.FECHADA.equals(estado); }
    public boolean isCancelada() { return Estado.CANCELADA.equals(estado); }

    public Integer         getId()             { return id; }
    public LocalDateTime   getDataHora()       { return dataHora; }
    public BigDecimal      getValorTotal()     { return valorTotal; }
    public String          getNomeCliente()    { return nomeCliente; }
    public String          getNomeFuncionario(){ return nomeFuncionario; }
    public Estado          getEstado()         { return estado; }
    public List<ItemVenda> getItens()          { return itens; }

    public void setId(Integer id)                  { this.id = id; }
    public void setDataHora(LocalDateTime d)       { this.dataHora = d; }
    public void setValorTotal(BigDecimal v)        { this.valorTotal = v; }
    public void setNomeCliente(String n)           { this.nomeCliente = n; }
    public void setNomeFuncionario(String n)       { this.nomeFuncionario = n; }
    public void setEstado(Estado e)                { this.estado = e; }
    public void setItens(List<ItemVenda> i)        { this.itens = i; }
}
