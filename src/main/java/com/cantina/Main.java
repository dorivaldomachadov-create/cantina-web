package cantina;

import cantina.repositorio.PedidoRepositorio;
import cantina.repositorio.ProdutoRepositorio;
import cantina.servico.CardapioServico;
import cantina.servico.EstoqueServico;
import cantina.servico.PedidoServico;
import cantina.util.Formatador;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final String RESET  = "\033[0m";
    private static final String BOLD   = "\033[1m";
    private static final String DIM    = "\033[2m";
    private static final String CIANO  = "\033[38;5;51m";
    private static final String VERDE  = "\033[38;5;82m";
    private static final String AMAR   = "\033[38;5;220m";
    private static final String LARANJA= "\033[38;5;214m";
    private static final String ROXO   = "\033[38;5;177m";
    private static final String AZUL   = "\033[38;5;75m";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        ProdutoRepositorio produtoRepositorio = new ProdutoRepositorio();
        PedidoRepositorio pedidoRepositorio   = new PedidoRepositorio();

        EstoqueServico estoqueServico   = new EstoqueServico(produtoRepositorio, scanner);
        CardapioServico cardapioServico = new CardapioServico(produtoRepositorio, scanner);
        PedidoServico pedidoServico     = new PedidoServico(pedidoRepositorio, produtoRepositorio, estoqueServico, scanner);

        mostrarBoasVindas(produtoRepositorio.contarProdutos());
        estoqueServico.alertarStockBaixo();

        int opcao = -1;
        while (opcao != 0) {
            mostrarMenuPrincipal();
            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                Formatador.erro("Introduz apenas um número.");
                scanner.nextLine();
                continue;
            }

            switch (opcao) {
                case 1 -> cardapioServico.menuCardapio();
                case 2 -> pedidoServico.menuPedidos();
                case 3 -> estoqueServico.menuEstoque();
                case 4 -> cardapioServico.mostrarCardapioCompleto();
                case 0 -> despedida();
                default -> Formatador.erro("Opção inválida. Escolhe entre 0 e 4.");
            }
        }

        scanner.close();
    }

    private static void mostrarBoasVindas(int totalProdutos) {
        System.out.println("\n\n");
        System.out.println("  " + CIANO + BOLD + "╔══════════════════════════════════════════════════════╗" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "                                                      " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "   " + LARANJA + BOLD + "  ██████╗ █████╗ ███╗   ██╗████████╗██╗███╗   ██╗  " + RESET + "  " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "   " + LARANJA + BOLD + " ██╔════╝██╔══██╗████╗  ██║╚══██╔══╝██║████╗  ██║  " + RESET + "  " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "   " + AMAR   + BOLD + " ██║     ███████║██╔██╗ ██║   ██║   ██║██╔██╗ ██║  " + RESET + "  " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "   " + VERDE  + BOLD + " ██║     ██╔══██║██║╚██╗██║   ██║   ██║██║╚██╗██║  " + RESET + "  " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "   " + AZUL   + BOLD + " ╚██████╗██║  ██║██║ ╚████║   ██║   ██║██║ ╚████║  " + RESET + "  " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "   " + ROXO   + BOLD + "  ╚═════╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ╚═╝╚═╝  ╚═══╝  " + RESET + " " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "                                                      " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "      " + DIM + "Sistema de Gestão  •  Java Puro  •  v2.0" + RESET + "       " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "             " + DIM + totalProdutos + " produtos carregados" + RESET + "                    " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET + "                                                      " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "╚══════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println(Formatador.titulo("MENU PRINCIPAL"));
        System.out.println();
        Formatador.opcaoMenu("1", "Gerir Cardápio",       "ver, adicionar, remover produtos");
        Formatador.opcaoMenu("2", "Gerir Pedidos",        "criar, adicionar itens, fechar, factura");
        Formatador.opcaoMenu("3", "Controlo de Estoque",  "ver stock, repor, alertas");
        Formatador.opcaoMenu("4", "Ver Cardápio Rápido",  "listagem completa de uma vez");
        System.out.println();
        Formatador.opcaoMenu("0", "Sair",                 "encerrar o sistema");
        Formatador.prompt("Opção:");
    }

    private static void despedida() {
        System.out.println();
        System.out.println("  " + CIANO + BOLD + "╔" + "═".repeat(54) + "╗" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET
                + "     " + AMAR + BOLD + "Obrigado por usar o Sistema de Cantina!" + RESET
                + "    " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET
                + "              " + VERDE + "Até à próxima! 👋" + RESET
                + "                   " + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "╚" + "═".repeat(54) + "╝" + RESET);
        System.out.println();
    }
}
