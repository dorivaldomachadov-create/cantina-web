package cantina.modelo;

/**
 * Subclasse de Produto para bebidas.
 * Adiciona o atributo "gelada".
 */
public class Bebida extends Produto {

    private boolean gelada;

    public Bebida(int id, String nome, double preco, int quantidadeEstoque, boolean gelada) {
        super(id, nome, preco, quantidadeEstoque, "Bebida");
        this.gelada = gelada;
    }

    public boolean isGelada() { return gelada; }
    public void setGelada(boolean gelada) { this.gelada = gelada; }

    @Override
    public String descricao() {
        String estado = gelada ? "Gelada" : "Natural";
        return String.format("[%d] %-26s Kz %8.2f  |  Estoque: %2d  |  Bebida (%s)",
                getId(), getNome(), getPreco(), getQuantidadeEstoque(), estado);
    }
}
