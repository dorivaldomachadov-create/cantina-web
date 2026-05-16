package cantina.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    public enum Estado { ABERTO, FECHADO }

    private int numero;
    private List<ItemPedido> itens;
    private Estado estado;
    private LocalDateTime dataHora;
    private String nomeCliente;

    public Pedido(int numero, String nomeCliente) {
        this.numero = numero;
        this.nomeCliente = nomeCliente;
        this.itens = new ArrayList<>();
        this.estado = Estado.ABERTO;
        this.dataHora = LocalDateTime.now();
    }

    public int getNumero()              { return numero; }
    public List<ItemPedido> getItens()  { return itens; }
    public Estado getEstado()           { return estado; }
    public LocalDateTime getDataHora()  { return dataHora; }
    public String getNomeCliente()      { return nomeCliente; }

    public boolean isAberto()  { return estado == Estado.ABERTO; }

    public void adicionarItem(ItemPedido novoItem) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == novoItem.getProduto().getId()) {
                item.setQuantidade(item.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        itens.add(novoItem);
    }

    public boolean removerItem(int idProduto) {
        return itens.removeIf(item -> item.getProduto().getId() == idProduto);
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.calcularSubtotal();
        }
        return total;
    }

    public void fechar() {
        this.estado = Estado.FECHADO;
    }

    public String getDataHoraFormatada() {
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String resumo() {
        String badge = estado == Estado.ABERTO ? "ABERTO" : "FECHADO";
        return String.format("  #%03d  %-15s  %d item(s)  Kz %.2f  [%s]",
                numero, nomeCliente, itens.size(), calcularTotal(), badge);
    }
}
