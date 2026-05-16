package cantina.servico;

import cantina.modelo.Produto;
import cantina.repositorio.ProdutoRepositorio;
import cantina.util.Formatador;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Serviço responsável pelo controlo de estoque.
 * Criado pelo membro: Elias Amadeu
 */
public class EstoqueServico {

    private static final int LIMITE_STOCK_BAIXO = 3;

    private ProdutoRepositorio repositorio;
    private Scanner scanner;

    public EstoqueServico(ProdutoRepositorio repositorio, Scanner scanner) {
        this.repositorio = repositorio;
        this.scanner = scanner;
    }

    // ---------- Menu ----------

    public void menuEstoque() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n" + Formatador.titulo("  CONTROLO DE ESTOQUE  "));
            System.out.println("  1. Ver estoque completo");
            System.out.println("  2. Ver produtos com stock baixo");
            System.out.println("  3. Repor stock de produto");
            System.out.println("  0. Voltar ao menu principal");
            System.out.print("\n  Escolhe uma opção: ");

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
                case 0 -> System.out.println("  A voltar ao menu principal...");
                default -> Formatador.erro("Opção inválida.");
            }
        }
    }

    // ---------- Operações ----------

    /**
     * Verifica se há quantidade suficiente em estoque.
     */
    public boolean verificarDisponibilidade(int idProduto, int quantidade) {
        Produto p = repositorio.buscarPorId(idProduto);
        if (p == null) return false;
        return p.getQuantidadeEstoque() >= quantidade;
    }

    /**
     * Diminui o stock após um pedido ser confirmado.
     */
    public boolean reduzirEstoque(int idProduto, int quantidade) {
        Produto p = repositorio.buscarPorId(idProduto);
        if (p == null || p.getQuantidadeEstoque() < quantidade) return false;
        p.setQuantidadeEstoque(p.getQuantidadeEstoque() - quantidade);
        return true;
    }

    /**
     * Devolve stock quando um pedido é cancelado.
     */
    public void devolverEstoque(int idProduto, int quantidade) {
        Produto p = repositorio.buscarPorId(idProduto);
        if (p != null) {
            p.setQuantidadeEstoque(p.getQuantidadeEstoque() + quantidade);
        }
    }

    private void verEstoqueCompleto() {
        List<Produto> produtos = repositorio.listarTodos();
        System.out.println("\n" + Formatador.titulo("  ESTOQUE COMPLETO  "));
        for (Produto p : produtos) {
            String alerta = p.getQuantidadeEstoque() <= LIMITE_STOCK_BAIXO ? "  ⚠️  STOCK BAIXO" : "";
            System.out.printf("  [%d] %-28s  Estoque: %3d%s%n",
                    p.getId(), p.getNome(), p.getQuantidadeEstoque(), alerta);
        }
    }

    public void alertarStockBaixo() {
        List<Produto> produtos = repositorio.listarTodos();
        System.out.println("\n" + Formatador.titulo("  PRODUTOS COM STOCK BAIXO  "));
        boolean algum = false;
        for (Produto p : produtos) {
            if (p.getQuantidadeEstoque() <= LIMITE_STOCK_BAIXO) {
                System.out.printf("  ⚠️  [%d] %-28s  Estoque: %d%n",
                        p.getId(), p.getNome(), p.getQuantidadeEstoque());
                algum = true;
            }
        }
        if (!algum) System.out.println("  Todos os produtos têm stock suficiente.");
    }

    private void reporEstoque() {
        verEstoqueCompleto();
        System.out.print("\n  ID do produto a repor (0 para cancelar): ");
        try {
            int id = scanner.nextInt();
            if (id == 0) { scanner.nextLine(); return; }
            Produto p = repositorio.buscarPorId(id);
            if (p == null) { Formatador.erro("Produto não encontrado."); scanner.nextLine(); return; }

            System.out.print("  Quantidade a adicionar: ");
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
