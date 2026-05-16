package cantina.repositorio;

import cantina.modelo.Bebida;
import cantina.modelo.Comida;
import cantina.modelo.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoRepositorio {

    private List<Produto> produtos;
    private int proximoId;

    public ProdutoRepositorio() {
        produtos = new ArrayList<>();
        proximoId = 1;
        carregarProdutosIniciais();
    }

    private void carregarProdutosIniciais() {
        produtos.add(new Comida(proximoId++, "Pão com manteiga",    150.00, 20, true));
        produtos.add(new Comida(proximoId++, "Cachorro quente",      350.00, 15, false));
        produtos.add(new Comida(proximoId++, "Sanduíche de frango",  500.00, 10, false));
        produtos.add(new Comida(proximoId++, "Feijão com arroz",     700.00, 12, true));
        produtos.add(new Comida(proximoId++, "Frango grelhado",     1200.00,  8, false));
        produtos.add(new Comida(proximoId++, "Salada de legumes",    400.00, 10, true));
        produtos.add(new Comida(proximoId++, "Pizza de queijo",      900.00,  6, true));
        produtos.add(new Bebida(proximoId++, "Água mineral (500ml)", 100.00, 30, false));
        produtos.add(new Bebida(proximoId++, "Sumo de laranja",      200.00, 25, true));
        produtos.add(new Bebida(proximoId++, "Refrigerante lata",    250.00, 20, true));
        produtos.add(new Bebida(proximoId++, "Sumo de manga",        250.00, 18, true));
        produtos.add(new Bebida(proximoId++, "Café",                 150.00, 15, false));
        produtos.add(new Bebida(proximoId++, "Chá natural",          120.00, 10, false));
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public List<Produto> listarPorCategoria(String categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Produto buscarPorId(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public Produto buscarPorNome(String nome) {
        for (Produto p : produtos) {
            if (p.getNome().toLowerCase().contains(nome.toLowerCase())) return p;
        }
        return null;
    }

    public void adicionar(Produto produto) {
        produto = criarComId(produto);
        produtos.add(produto);
    }

    private Produto criarComId(Produto p) {
        if (p instanceof Bebida b) {
            return new Bebida(proximoId++, b.getNome(), b.getPreco(), b.getQuantidadeEstoque(), b.isGelada());
        } else if (p instanceof Comida c) {
            return new Comida(proximoId++, c.getNome(), c.getPreco(), c.getQuantidadeEstoque(), c.isVegetariana());
        }
        return new Produto(proximoId++, p.getNome(), p.getPreco(), p.getQuantidadeEstoque(), p.getCategoria());
    }

    public boolean remover(int id) {
        return produtos.removeIf(p -> p.getId() == id);
    }

    public int contarProdutos() {
        return produtos.size();
    }
}
