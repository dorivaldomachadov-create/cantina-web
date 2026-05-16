package cantina.modelo;

public class Produto {

    private int id;
    private String nome;
    private double preco;
    private int quantidadeEstoque;
    private String categoria;

    public Produto(int id, String nome, double preco, int quantidadeEstoque, String categoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
    }

    public int getId()                  { return id; }
    public String getNome()             { return nome; }
    public double getPreco()            { return preco; }
    public int getQuantidadeEstoque()   { return quantidadeEstoque; }
    public String getCategoria()        { return categoria; }

    public void setNome(String nome)                  { this.nome = nome; }
    public void setPreco(double preco)                { this.preco = preco; }
    public void setQuantidadeEstoque(int qtd)         { this.quantidadeEstoque = qtd; }
    public void setCategoria(String categoria)        { this.categoria = categoria; }

    public String descricao() {
        return String.format("[%d] %-26s Kz %8.2f  |  Estoque: %2d  |  %s",
                id, nome, preco, quantidadeEstoque, categoria);
    }

    @Override
    public String toString() {
        return descricao();
    }
}
