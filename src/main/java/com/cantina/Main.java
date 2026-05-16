package cantina;
import cantina.repositorio.PedidoRepositorio;
import cantina.repositorio.ProdutoRepositorio;
import cantina.servico.CardapioServico;
import cantina.servico.EstoqueServico;
import cantina.servico.PedidoServico;
import cantina.util.Formatador;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════╗
 * ║   SISTEMA DE GESTÃO DE CANTINA — Java Puro   ║
 * ║   Desenvolvido por: Dorivaldo Machado        ║
 * ╚══════════════════════════════════════════════╝
 */
public class                                                                                        Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        ProdutoRepositorio produtoRepositorio = new ProdutoRepositorio();
        PedidoRepositorio pedidoRepositorio   = new PedidoRepositorio();

        EstoqueServico estoqueServico   = new EstoqueServico(produtoRepositorio, scanner);
        CardapioServico cardapioServico = new CardapioServico(produtoRepositorio, scanner);
        PedidoServico pedidoServico     = new PedidoServico(pedidoRepositorio, produtoRepositorio, estoqueServico, scanner);

        // Ecrã de boas-vindas
        mostrarBoasVindas();

        // Aviso de stock baixo ao iniciar
        estoqueServico.alertarStockBaixo();

        // Menu principal
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

    private static void mostrarBoasVindas() {
        System.out.println("\n\n");
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║       🍽️  SISTEMA DE GESTÃO DE CANTINA 🍽️           ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║         Bem-vindo ao sistema de gestão!              ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n" + Formatador.titulo("  MENU PRINCIPAL  "));
        System.out.println();
        System.out.println("  1.  Gerir Cardápio        (ver, adicionar, remover produtos)");
        System.out.println("  2.  Gerir Pedidos         (criar, adicionar itens, fechar, factura)");
        System.out.println("  3.  Controlo de Estoque   (ver stock, repor, alertas)");
        System.out.println("  4.  Ver Cardápio Rápido   (ver tudo de uma vez)");
        System.out.println();
        System.out.println("  0.  Sair do sistema");
        System.out.println();
        System.out.print("  Escolhe uma opção: ");
    }

    private static void despedida() {
        System.out.println("\n" + Formatador.linha());
        System.out.println("      Obrigado por usar o Sistema de Gestão de Cantina!");
        System.out.println("                    Até à próxima!");
        System.out.println(Formatador.linha());
        System.out.println();
    }
}
