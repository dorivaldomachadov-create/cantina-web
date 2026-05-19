//package cantina.modelo;

//import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um pedido feito na cantina.
 * Contém uma lista de itens e o estado (aberto / fechado).
 */
/*public class Pedido {

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

    // ---------- Getters ----------
    public int getNumero()              { return numero; }
    public List<ItemPedido> getItens()  { return itens; }
    public Estado getEstado()           { return estado; }
    public LocalDateTime getDataHora()  { return dataHora; }
    public String getNomeCliente()      { return nomeCliente; }

    public boolean isAberto()  { return estado == Estado.ABERTO; }*/

    /**
     * Adiciona um item ao pedido.
     * Se o produto já existir, incrementa a quantidade.
     */
    /*public void adicionarItem(ItemPedido novoItem) {
        for (ItemPedido item : itens) {
            if (item.getProduto().getId() == novoItem.getProduto().getId()) {
                item.setQuantidade(item.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        itens.add(novoItem);
    }*/

    /**
     * Remove um item do pedido pelo ID do produto.
     */
    /*public boolean removerItem(int idProduto) {
        return itens.removeIf(item -> item.getProduto().getId() == idProduto);
    }*/

    /**
     * Calcula o total do pedido somando todos os subtotais.
     */
    /*public double calcularTotal() {
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
        return String.format("Pedido #%03d  |  Cliente: %-15s  |  Itens: %d  |  Total: Kz %.2f  |  %s",
                numero, nomeCliente, itens.size(), calcularTotal(), estado);
    }
}
*/