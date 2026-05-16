package cantina.servico;

import cantina.modelo.ItemPedido;
import cantina.modelo.Pedido;
import cantina.modelo.Produto;
import cantina.repositorio.PedidoRepositorio;
import cantina.repositorio.ProdutoRepositorio;
import cantina.util.Formatador;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Serviço responsável pela gestão de pedidos.
 */
public class PedidoServico {

    private PedidoRepositorio pedidoRepositorio;
    private ProdutoRepositorio produtoRepositorio;
    private EstoqueServico estoqueServico;
    private Scanner scanner;

    public PedidoServico(PedidoRepositorio pedidoRepositorio,
                         ProdutoRepositorio produtoRepositorio,
                         EstoqueServico estoqueServico,
                         Scanner scanner) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.produtoRepositorio = produtoRepositorio;
        this.estoqueServico = estoqueServico;
        this.scanner = scanner;
    }

    // ---------- Menus ----------

    public void menuPedidos() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n" + Formatador.titulo("  GESTÃO DE PEDIDOS  "));
            System.out.println("  1. Novo pedido");
            System.out.println("  2. Adicionar item a pedido existente");
            System.out.println("  3. Remover item de um pedido");
            System.out.println("  4. Ver pedidos em aberto");
            System.out.println("  5. Ver todos os pedidos");
            System.out.println("  6. Fechar pedido e gerar factura");
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
                case 1 -> novoPedido();
                case 2 -> adicionarItemAPedido();
                case 3 -> removerItemDePedido();
                case 4 -> listarPedidosAbertos();
                case 5 -> listarTodosPedidos();
                case 6 -> fecharPedidoEGerarFactura();
                case 0 -> System.out.println("  A voltar ao menu principal...");
                default -> Formatador.erro("Opção inválida. Escolhe entre 0 e 6.");
            }
        }
    }

    // ---------- Operações ----------

    private void novoPedido() {
        System.out.println("\n" + Formatador.subtitulo("  NOVO PEDIDO  "));
        System.out.print("  Nome do cliente: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) nome = "Cliente";

        Pedido pedido = pedidoRepositorio.criar(nome);
        Formatador.sucesso("Pedido #" + String.format("%03d", pedido.getNumero()) + " criado para " + nome + "!");

        // Oferecer imediatamente adicionar itens
        System.out.print("  Queres adicionar itens agora? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            adicionarItens(pedido);
        }
    }

    private void adicionarItemAPedido() {
        Pedido pedido = selecionarPedidoAberto();
        if (pedido == null) return;
        adicionarItens(pedido);
    }

    private void adicionarItens(Pedido pedido) {
        System.out.println("\n" + Formatador.subtitulo("  PEDIDO #" + String.format("%03d", pedido.getNumero()) + " — ADICIONAR ITENS  "));

        // Mostrar cardápio resumido
        List<Produto> todos = produtoRepositorio.listarTodos();
        System.out.println("\n  === CARDÁPIO ===");
        for (Produto p : todos) {
            if (p.getQuantidadeEstoque() > 0) {
                System.out.println("  " + p.descricao());
            }
        }

        boolean continuar = true;
        while (continuar) {
            System.out.print("\n  ID do produto (0 para terminar): ");
            try {
                int idProduto = scanner.nextInt();
                if (idProduto == 0) { scanner.nextLine(); break; }

                Produto produto = produtoRepositorio.buscarPorId(idProduto);
                if (produto == null) {
                    Formatador.erro("Produto não encontrado.");
                    scanner.nextLine();
                    continue;
                }
                if (produto.getQuantidadeEstoque() == 0) {
                    Formatador.aviso("\"" + produto.getNome() + "\" está sem stock.");
                    scanner.nextLine();
                    continue;
                }

                System.out.print("  Quantidade: ");
                int qtd = scanner.nextInt();
                scanner.nextLine();

                if (qtd <= 0) { Formatador.erro("Quantidade inválida."); continue; }

                if (!estoqueServico.verificarDisponibilidade(idProduto, qtd)) {
                    Formatador.aviso("Stock insuficiente. Disponível: " + produto.getQuantidadeEstoque());
                    continue;
                }

                pedido.adicionarItem(new ItemPedido(produto, qtd));
                estoqueServico.reduzirEstoque(idProduto, qtd);
                Formatador.sucesso(qtd + "x \"" + produto.getNome() + "\" adicionado(s)!");

                System.out.print("  Adicionar mais itens? (s/n): ");
                continuar = scanner.nextLine().trim().equalsIgnoreCase("s");

            } catch (InputMismatchException e) {
                Formatador.erro("Valor inválido.");
                scanner.nextLine();
            }
        }

        if (!pedido.getItens().isEmpty()) {
            System.out.println("\n  " + Formatador.linhaMenor());
            System.out.println("  Subtotal actual: " + Formatador.moeda(pedido.calcularTotal()));
        }
    }

    private void removerItemDePedido() {
        Pedido pedido = selecionarPedidoAberto();
        if (pedido == null) return;

        if (pedido.getItens().isEmpty()) {
            Formatador.aviso("Este pedido não tem itens.");
            return;
        }

        System.out.println("\n  Itens do pedido #" + String.format("%03d", pedido.getNumero()) + ":");
        for (ItemPedido item : pedido.getItens()) {
            System.out.println("  " + item.descricao() + "  [ID: " + item.getProduto().getId() + "]");
        }

        System.out.print("\n  ID do produto a remover (0 para cancelar): ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();
            if (id == 0) return;

            // Devolver ao estoque antes de remover
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProduto().getId() == id) {
                    estoqueServico.devolverEstoque(id, item.getQuantidade());
                    break;
                }
            }

            if (pedido.removerItem(id)) {
                Formatador.sucesso("Item removido e stock devolvido.");
            } else {
                Formatador.erro("Item não encontrado no pedido.");
            }
        } catch (InputMismatchException e) {
            Formatador.erro("Valor inválido.");
            scanner.nextLine();
        }
    }

    private void listarPedidosAbertos() {
        List<Pedido> abertos = pedidoRepositorio.listarAbertos();
        System.out.println("\n" + Formatador.titulo("  PEDIDOS EM ABERTO  "));
        if (abertos.isEmpty()) {
            System.out.println("  Nenhum pedido em aberto.");
            return;
        }
        for (Pedido p : abertos) {
            System.out.println("  " + p.resumo());
        }
    }

    private void listarTodosPedidos() {
        List<Pedido> todos = pedidoRepositorio.listarTodos();
        System.out.println("\n" + Formatador.titulo("  TODOS OS PEDIDOS  "));
        if (todos.isEmpty()) {
            System.out.println("  Nenhum pedido registado.");
            return;
        }
        for (Pedido p : todos) {
            System.out.println("  " + p.resumo());
        }
    }

    private void fecharPedidoEGerarFactura() {
        Pedido pedido = selecionarPedidoAberto();
        if (pedido == null) return;

        if (pedido.getItens().isEmpty()) {
            Formatador.aviso("Não é possível fechar um pedido sem itens.");
            return;
        }

        System.out.print("  Confirmas o fecho do pedido #" + String.format("%03d", pedido.getNumero()) + "? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("  Operação cancelada.");
            return;
        }

        pedido.fechar();
        imprimirFactura(pedido);
    }

    // ---------- Factura ----------

    public void imprimirFactura(Pedido pedido) {
        System.out.println("\n\n" + Formatador.linha());
        System.out.println("          CANTINA — FACTURA OFICIAL");
        System.out.println(Formatador.linha());
        System.out.printf("  Pedido Nº:  #%03d%n", pedido.getNumero());
        System.out.println("  Cliente:    " + pedido.getNomeCliente());
        System.out.println("  Data/Hora:  " + pedido.getDataHoraFormatada());
        System.out.println(Formatador.linhaMenor());
        System.out.println("  ITENS:");
        System.out.println(Formatador.linhaMenor());

        for (ItemPedido item : pedido.getItens()) {
            System.out.println("  " + item.descricao());
        }

        System.out.println(Formatador.linhaMenor());
        System.out.println(Formatador.linhaFactura("  TOTAL A PAGAR:", pedido.calcularTotal()));
        System.out.println(Formatador.linha());
        System.out.println("        Obrigado pela preferência! Volte sempre!");
        System.out.println(Formatador.linha());
        System.out.println();
    }

    // ---------- Utilitário ----------

    private Pedido selecionarPedidoAberto() {
        List<Pedido> abertos = pedidoRepositorio.listarAbertos();
        if (abertos.isEmpty()) {
            Formatador.aviso("Não há pedidos em aberto. Cria um pedido primeiro.");
            return null;
        }
        System.out.println("\n  Pedidos em aberto:");
        for (Pedido p : abertos) {
            System.out.println("  " + p.resumo());
        }
        System.out.print("\n  Número do pedido (0 para cancelar): ");
        try {
            int num = scanner.nextInt();
            scanner.nextLine();
            if (num == 0) return null;
            Pedido pedido = pedidoRepositorio.buscarPorNumero(num);
            if (pedido == null || !pedido.isAberto()) {
                Formatador.erro("Pedido não encontrado ou já fechado.");
                return null;
            }
            return pedido;
        } catch (InputMismatchException e) {
            Formatador.erro("Número inválido.");
            scanner.nextLine();
            return null;
        }
    }
}
