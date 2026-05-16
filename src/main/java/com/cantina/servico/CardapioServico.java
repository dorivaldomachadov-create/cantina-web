package cantina.servico;

import cantina.modelo.Bebida;
import cantina.modelo.Comida;
import cantina.modelo.Produto;
import cantina.repositorio.ProdutoRepositorio;
import cantina.util.Formatador;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CardapioServico {

    private ProdutoRepositorio repositorio;
    private Scanner scanner;

    public CardapioServico(ProdutoRepositorio repositorio, Scanner scanner) {
        this.repositorio = repositorio;
        this.scanner = scanner;
    }

    public void menuCardapio() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println(Formatador.titulo("GESTÃO DO CARDÁPIO"));
            System.out.println();
            Formatador.opcaoMenu("1", "Cardápio completo",          "ver todos os produtos");
            Formatador.opcaoMenu("2", "Apenas comidas",             "filtrar por categoria");
            Formatador.opcaoMenu("3", "Apenas bebidas",             "filtrar por categoria");
            Formatador.opcaoMenu("4", "Procurar por nome",          "pesquisar produto");
            Formatador.opcaoMenu("5", "Adicionar produto",          "inserir novo item no cardápio");
            Formatador.opcaoMenu("6", "Remover produto",            "retirar item do cardápio");
            System.out.println();
            Formatador.opcaoMenu("0", "Voltar",                     "menu principal");
            Formatador.prompt("Opção:");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                Formatador.erro("Introduz apenas um número.");
                scanner.nextLine();
                continue;
            }

            switch (opcao) {
                case 1 -> mostrarCardapioCompleto();
                case 2 -> mostrarPorCategoria("Comida");
                case 3 -> mostrarPorCategoria("Bebida");
                case 4 -> procurarPorNome();
                case 5 -> adicionarProduto();
                case 6 -> removerProduto();
                case 0 -> {}
                default -> Formatador.erro("Opção inválida. Escolhe entre 0 e 6.");
            }
        }
    }

    public void mostrarCardapioCompleto() {
        List<Produto> produtos = repositorio.listarTodos();
        System.out.println(Formatador.titulo("CARDÁPIO COMPLETO"));

        if (produtos.isEmpty()) {
            Formatador.aviso("Cardápio vazio.");
            return;
        }

        System.out.println(Formatador.secao("Comidas"));
        boolean temComida = false;
        for (Produto p : produtos) {
            if (p.getCategoria().equalsIgnoreCase("Comida")) {
                imprimirLinhaProduto(p);
                temComida = true;
            }
        }
        if (!temComida) Formatador.info("Sem comidas disponíveis.");

        System.out.println(Formatador.secao("Bebidas"));
        boolean temBebida = false;
        for (Produto p : produtos) {
            if (p.getCategoria().equalsIgnoreCase("Bebida")) {
                imprimirLinhaProduto(p);
                temBebida = true;
            }
        }
        if (!temBebida) Formatador.info("Sem bebidas disponíveis.");

        System.out.println();
        System.out.println("  " + Formatador.linhaMenor());
        Formatador.info("Total de produtos: " + produtos.size());
        System.out.println();
    }

    private void imprimirLinhaProduto(Produto p) {
        String RESET = "\033[0m";
        String BOLD  = "\033[1m";
        String DIM   = "\033[2m";
        String VERDE = "\033[38;5;82m";
        String CIANO = "\033[38;5;51m";
        String AMAR  = "\033[38;5;220m";

        String stockCor = p.getQuantidadeEstoque() <= 3
                ? AMAR + BOLD
                : VERDE;

        String extra = "";
        if (p instanceof Comida c) {
            extra = c.isVegetariana() ? DIM + " 🌿 veg" + RESET : DIM + " 🥩 carne" + RESET;
        } else if (p instanceof Bebida b) {
            extra = b.isGelada() ? DIM + " ❄ gelada" + RESET : DIM + " 🌡 natural" + RESET;
        }

        System.out.printf("  %s[%2d]%s  %-26s  %s%-10s%s  estoque: %s%2d%s%s%n",
                DIM + CIANO, p.getId(), RESET,
                p.getNome(),
                BOLD + VERDE, String.format("Kz %.0f", p.getPreco()), RESET,
                stockCor, p.getQuantidadeEstoque(), RESET,
                extra);
    }

    public void mostrarPorCategoria(String categoria) {
        List<Produto> produtos = repositorio.listarPorCategoria(categoria);
        System.out.println(Formatador.titulo(categoria.toUpperCase() + "S"));
        System.out.println();
        if (produtos.isEmpty()) {
            Formatador.aviso("Nenhum(a) " + categoria.toLowerCase() + " disponível.");
            return;
        }
        for (Produto p : produtos) {
            imprimirLinhaProduto(p);
        }
        System.out.println();
    }

    private void procurarPorNome() {
        Formatador.prompt("Nome a procurar:");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) { Formatador.erro("Nome vazio."); return; }
        Produto p = repositorio.buscarPorNome(nome);
        if (p == null) {
            Formatador.erro("Nenhum produto encontrado com esse nome.");
        } else {
            System.out.println(Formatador.subtitulo("Produto encontrado"));
            System.out.println();
            imprimirLinhaProduto(p);
            System.out.println();
        }
    }

    private void adicionarProduto() {
        System.out.println(Formatador.subtitulo("ADICIONAR PRODUTO"));
        System.out.println();
        try {
            Formatador.prompt("Nome do produto:");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) { Formatador.erro("Nome inválido."); return; }

            Formatador.prompt("Preço (Kz):");
            double preco = scanner.nextDouble();
            if (preco <= 0) { Formatador.erro("Preço tem de ser positivo."); scanner.nextLine(); return; }

            Formatador.prompt("Quantidade em estoque:");
            int qtd = scanner.nextInt();
            if (qtd < 0) { Formatador.erro("Quantidade inválida."); scanner.nextLine(); return; }

            Formatador.prompt("Categoria  [ 1 Comida  |  2 Bebida ]:");
            int cat = scanner.nextInt();
            scanner.nextLine();

            if (cat == 1) {
                Formatador.prompt("É vegetariana? (s/n):");
                boolean veg = scanner.nextLine().trim().equalsIgnoreCase("s");
                repositorio.adicionar(new Comida(0, nome, preco, qtd, veg));
            } else if (cat == 2) {
                Formatador.prompt("É gelada? (s/n):");
                boolean gel = scanner.nextLine().trim().equalsIgnoreCase("s");
                repositorio.adicionar(new Bebida(0, nome, preco, qtd, gel));
            } else {
                Formatador.erro("Categoria inválida.");
                return;
            }
            Formatador.sucesso("\"" + nome + "\" adicionado ao cardápio!");

        } catch (InputMismatchException e) {
            Formatador.erro("Valor inválido introduzido.");
            scanner.nextLine();
        }
    }

    private void removerProduto() {
        mostrarCardapioCompleto();
        Formatador.prompt("ID do produto a remover (0 para cancelar):");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();
            if (id == 0) return;
            Produto p = repositorio.buscarPorId(id);
            if (p == null) { Formatador.erro("Produto não encontrado."); return; }
            Formatador.prompt("Confirmas remoção de \"" + p.getNome() + "\"? (s/n):");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                repositorio.remover(id);
                Formatador.sucesso("Produto removido com sucesso.");
            } else {
                Formatador.info("Operação cancelada.");
            }
        } catch (InputMismatchException e) {
            Formatador.erro("Introduz um número válido.");
            scanner.nextLine();
        }
    }

    public Produto buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public List<Produto> listarTodos() {
        return repositorio.listarTodos();
    }
}
