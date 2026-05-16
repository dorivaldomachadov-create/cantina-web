package cantina.servico;

import cantina.modelo.Produto;
import cantina.repositorio.ProdutoRepositorio;
import cantina.util.Formatador;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class EstoqueServico {

    private static final int LIMITE_STOCK_BAIXO = 3;

    private ProdutoRepositorio repositorio;
    private Scanner scanner;

    public EstoqueServico(ProdutoRepositorio repositorio, Scanner scanner) {
        this.repositorio = repositorio;
        this.scanner = scanner;
    }

    public void menuEstoque() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println(Formatador.titulo("CONTROLO DE ESTOQUE"));
            System.out.println();
            Formatador.opcaoMenu("1", "Ver estoque completo",       "lista todos os produtos e quantidades");
            Formatador.opcaoMenu("2", "Produtos com stock baixo",   "itens com stock ≤ " + LIMITE_STOCK_BAIXO);
            Formatador.opcaoMenu("3", "Repor stock",                "adicionar unidades a um produto");
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
                case 1 -> verEstoqueCompleto();
                case 2 -> alertarStockBaixo();
                case 3 -> reporEstoque();
                case 0 -> {}
                default -> Formatador.erro("Opção inválida.");
            }
        }
    }

    public boolean verificarDisponibilidade(int idProduto, int quantidade) {
        Produto p = repositorio.buscarPorId(idProduto);
        if (p == null) return false;
        return p.getQuantidadeEstoque() >= quantidade;
    }

    public boolean reduzirEstoque(int idProduto, int quantidade) {
        Produto p = repositorio.buscarPorId(idProduto);
        if (p == null || p.getQuantidadeEstoque() < quantidade) return false;
        p.setQuantidadeEstoque(p.getQuantidadeEstoque() - quantidade);
        return true;
    }

    public void devolverEstoque(int idProduto, int quantidade) {
        Produto p = repositorio.buscarPorId(idProduto);
        if (p != null) {
            p.setQuantidadeEstoque(p.getQuantidadeEstoque() + quantidade);
        }
    }

    private void verEstoqueCompleto() {
        List<Produto> produtos = repositorio.listarTodos();
        System.out.println(Formatador.titulo("ESTOQUE COMPLETO"));
        System.out.println();

        String RESET  = "\033[0m";
        String BOLD   = "\033[1m";
        String DIM    = "\033[2m";
        String VERDE  = "\033[38;5;82m";
        String AMAR   = "\033[38;5;220m";
        String VERM   = "\033[38;5;196m";
        String CIANO  = "\033[38;5;51m";

        System.out.printf("  %s%-4s  %-28s  %-10s  %s%s%n",
                BOLD + DIM, "ID", "Produto", "Estoque", "Status", RESET);
        System.out.println("  " + DIM + "─".repeat(54) + RESET);

        for (Produto p : produtos) {
            int qtd = p.getQuantidadeEstoque();
            String cor   = qtd == 0 ? VERM : qtd <= LIMITE_STOCK_BAIXO ? AMAR : VERDE;
            String badge = qtd == 0 ? "  SEM STOCK" : qtd <= LIMITE_STOCK_BAIXO ? "  BAIXO" : "  OK";
            System.out.printf("  %s[%2d]%s  %-28s  %s%3d un%s   %s%s%s%n",
                    DIM + CIANO, p.getId(), RESET,
                    p.getNome(),
                    cor + BOLD, qtd, RESET,
                    cor + BOLD, badge, RESET);
        }
        System.out.println();
    }

    public void alertarStockBaixo() {
        List<Produto> produtos = repositorio.listarTodos();
        System.out.println(Formatador.titulo("PRODUTOS COM STOCK BAIXO"));
        System.out.println();

        String RESET = "\033[0m";
        String BOLD  = "\033[1m";
        String AMAR  = "\033[38;5;220m";
        String DIM   = "\033[2m";

        boolean algum = false;
        for (Produto p : produtos) {
            if (p.getQuantidadeEstoque() <= LIMITE_STOCK_BAIXO) {
                System.out.printf("  %s⚠%s  %-28s  %s%d unidade(s)%s%n",
                        AMAR + BOLD, RESET,
                        p.getNome(),
                        AMAR + BOLD, p.getQuantidadeEstoque(), RESET);
                algum = true;
            }
        }
        if (!algum) Formatador.sucesso("Todos os produtos têm stock suficiente.");
        System.out.println();
    }

    private void reporEstoque() {
        verEstoqueCompleto();
        Formatador.prompt("ID do produto a repor (0 para cancelar):");
        try {
            int id = scanner.nextInt();
            if (id == 0) { scanner.nextLine(); return; }
            Produto p = repositorio.buscarPorId(id);
            if (p == null) { Formatador.erro("Produto não encontrado."); scanner.nextLine(); return; }

            Formatador.prompt("Quantidade a adicionar:");
            int qtd = scanner.nextInt();
            scanner.nextLine();
            if (qtd <= 0) { Formatador.erro("Quantidade tem de ser positiva."); return; }

            p.setQuantidadeEstoque(p.getQuantidadeEstoque() + qtd);
            Formatador.sucesso("Stock de \"" + p.getNome() + "\" reposto! Novo stock: " + p.getQuantidadeEstoque());

        } catch (InputMismatchException e) {
            Formatador.erro("Valor inválido.");
            scanner.nextLine();
        }
    }
}
