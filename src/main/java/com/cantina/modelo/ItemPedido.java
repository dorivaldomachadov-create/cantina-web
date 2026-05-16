package cantina.modelo;

/**
 * Representa um item dentro de um pedido.
 * Liga um Produto a uma quantidade pedida.
 */
public class ItemPedido {

    private Produto produto;
    private int quantidade;

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto()     { return produto; }
    public int getQuantidade()      { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    /**
     * Calcula o subtotal deste item (preço × quantidade).
     */
    public double calcularSubtotal() {
        return produto.getPreco() * quantidade;
    }

    public String descricao() {
        return String.format("  %dx  %-26s  Kz %8.2f",
                quantidade, produto.getNome(), calcularSubtotal());
    }
}
