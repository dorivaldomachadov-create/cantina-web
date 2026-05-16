package cantina.modelo;

/**
 * Subclasse de Produto para comidas.
 * Adiciona o atributo "vegetariana".
 */
public class Comida extends Produto {

    private boolean vegetariana;

    public Comida(int id, String nome, double preco, int quantidadeEstoque, boolean vegetariana) {
        super(id, nome, preco, quantidadeEstoque, "Comida");
        this.vegetariana = vegetariana;
    }

    public boolean isVegetariana() { return vegetariana; }
    public void setVegetariana(boolean vegetariana) { this.vegetariana = vegetariana; }

    @Override
    public String descricao() {
        String tipo = vegetariana ? "Vegetariana" : "Com carne";
        return String.format("[%d] %-26s Kz %8.2f  |  Estoque: %2d  |  Comida (%s)",
                getId(), getNome(), getPreco(), getQuantidadeEstoque(), tipo);
    }
}
