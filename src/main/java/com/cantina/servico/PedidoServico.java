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

    public void menuPedidos() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println(Formatador.titulo("GESTÃO DE PEDIDOS"));
            System.out.println();
            Formatador.opcaoMenu("1", "Novo pedido",                  "criar pedido para um cliente");
            Formatador.opcaoMenu("2", "Adicionar item a pedido",      "inserir produto num pedido aberto");
            Formatador.opcaoMenu("3", "Remover item de pedido",       "retirar produto de um pedido aberto");
            Formatador.opcaoMenu("4", "Pedidos em aberto",            "listar pedidos pendentes");
            Formatador.opcaoMenu("5", "Todos os pedidos",             "histórico completo");
            Formatador.opcaoMenu("6", "Fechar pedido e gerar factura","finalizar e imprimir");
            System.out.println();
            Formatador.opcaoMenu("0", "Voltar",                       "menu principal");
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
                case 1 -> novoPedido();
                case 2 -> adicionarItemAPedido();
                case 3 -> removerItemDePedido();
                case 4 -> listarPedidosAbertos();
                case 5 -> listarTodosPedidos();
                case 6 -> fecharPedidoEGerarFactura();
                case 0 -> {}
                default -> Formatador.erro("Opção inválida. Escolhe entre 0 e 6.");
            }
        }
    }

    private void novoPedido() {
        System.out.println(Formatador.subtitulo("NOVO PEDIDO"));
        System.out.println();
        Formatador.prompt("Nome do cliente:");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) nome = "Cliente";

        Pedido pedido = pedidoRepositorio.criar(nome);
        Formatador.sucesso("Pedido #" + String.format("%03d", pedido.getNumero()) + " criado para " + nome + "!");

        Formatador.prompt("Adicionar itens agora? (s/n):");
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
        System.out.println(Formatador.subtitulo("PEDIDO #" + String.format("%03d", pedido.getNumero()) + " — ADICIONAR ITENS"));

        List<Produto> todos = produtoRepositorio.listarTodos();

        String RESET = "\033[0m";
        String BOLD  = "\033[1m";
        String DIM   = "\033[2m";
        String VERDE = "\033[38;5;82m";
        String CIANO = "\033[38;5;51m";
        String AMAR  = "\033[38;5;220m";

        System.out.println(Formatador.secao("Cardápio Disponível"));
        System.out.printf("  %s%-4s  %-28s  %-12s  %s%s%n",
                BOLD + DIM, "ID", "Produto", "Preço", "Stock", RESET);
        System.out.println("  " + DIM + "─".repeat(54) + RESET);

        for (Produto p : todos) {
            if (p.getQuantidadeEstoque() > 0) {
                String stockCor = p.getQuantidadeEstoque() <= 3 ? AMAR + BOLD : VERDE;
                System.out.printf("  %s[%2d]%s  %-28s  %s%-12s%s  %s%d un%s%n",
                        DIM + CIANO, p.getId(), RESET,
                        p.getNome(),
                        BOLD + VERDE, String.format("Kz %.0f", p.getPreco()), RESET,
                        stockCor, p.getQuantidadeEstoque(), RESET);
            }
        }
        System.out.println();

        boolean continuar = true;
        while (continuar) {
            Formatador.prompt("ID do produto (0 para terminar):");
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

                Formatador.prompt("Quantidade:");
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

                Formatador.prompt("Adicionar mais itens? (s/n):");
                continuar = scanner.nextLine().trim().equalsIgnoreCase("s");

            } catch (InputMismatchException e) {
                Formatador.erro("Valor inválido.");
                scanner.nextLine();
            }
        }

        if (!pedido.getItens().isEmpty()) {
            System.out.println();
            System.out.println("  " + Formatador.linhaMenor());
            System.out.println("  Subtotal actual: " + Formatador.moeda(pedido.calcularTotal()));
            System.out.println();
        }
    }

    private void removerItemDePedido() {
        Pedido pedido = selecionarPedidoAberto();
        if (pedido == null) return;

        if (pedido.getItens().isEmpty()) {
            Formatador.aviso("Este pedido não tem itens.");
            return;
        }

        String RESET = "\033[0m";
        String DIM   = "\033[2m";
        String CIANO = "\033[38;5;51m";
        String BOLD  = "\033[1m";

        System.out.println(Formatador.subtitulo("ITENS DO PEDIDO #" + String.format("%03d", pedido.getNumero())));
        System.out.println();
        for (ItemPedido item : pedido.getItens()) {
            System.out.printf("  %s[ID %2d]%s  %dx %-24s  Kz %.2f%n",
                    DIM + CIANO, item.getProduto().getId(), RESET,
                    item.getQuantidade(), item.getProduto().getNome(),
                    item.calcularSubtotal());
        }

        Formatador.prompt("ID do produto a remover (0 para cancelar):");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();
            if (id == 0) return;

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
        System.out.println(Formatador.titulo("PEDIDOS EM ABERTO"));
        System.out.println();
        if (abertos.isEmpty()) {
            Formatador.info("Nenhum pedido em aberto.");
            System.out.println();
            return;
        }
        for (Pedido p : abertos) {
            System.out.println(p.resumo());
        }
        System.out.println();
    }

    private void listarTodosPedidos() {
        List<Pedido> todos = pedidoRepositorio.listarTodos();
        System.out.println(Formatador.titulo("TODOS OS PEDIDOS"));
        System.out.println();
        if (todos.isEmpty()) {
            Formatador.info("Nenhum pedido registado.");
            System.out.println();
            return;
        }
        for (Pedido p : todos) {
            System.out.println(p.resumo());
        }
        System.out.println();
    }

    private void fecharPedidoEGerarFactura() {
        Pedido pedido = selecionarPedidoAberto();
        if (pedido == null) return;

        if (pedido.getItens().isEmpty()) {
            Formatador.aviso("Não é possível fechar um pedido sem itens.");
            return;
        }

        Formatador.prompt("Confirmas o fecho do pedido #" + String.format("%03d", pedido.getNumero()) + "? (s/n):");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            Formatador.info("Operação cancelada.");
            return;
        }

        pedido.fechar();
        imprimirFactura(pedido);
    }

    public void imprimirFactura(Pedido pedido) {
        String RESET  = "\033[0m";
        String BOLD   = "\033[1m";
        String DIM    = "\033[2m";
        String VERDE  = "\033[38;5;82m";
        String CIANO  = "\033[38;5;51m";
        String AMAR   = "\033[38;5;220m";
        String LARANJA= "\033[38;5;214m";

        System.out.println();
        System.out.println("  " + CIANO + BOLD + "╔" + "═".repeat(54) + "╗" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET
                + centrar("🧾  CANTINA — FACTURA OFICIAL", 54)
                + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "╠" + "═".repeat(54) + "╣" + RESET);

        String linhaPedido  = String.format("  Pedido Nº:  #%03d", pedido.getNumero());
        String linhaCliente = "  Cliente:    " + pedido.getNomeCliente();
        String linhaData    = "  Data/Hora:  " + pedido.getDataHoraFormatada();

        for (String linha : new String[]{linhaPedido, linhaCliente, linhaData}) {
            int pad = 54 - linha.length();
            System.out.println("  " + CIANO + BOLD + "║" + RESET
                    + BOLD + linha + " ".repeat(Math.max(0, pad)) + RESET
                    + CIANO + BOLD + "║" + RESET);
        }

        System.out.println("  " + CIANO + "╠" + "─".repeat(54) + "╣" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET
                + LARANJA + BOLD + "  ITENS" + " ".repeat(48) + RESET
                + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + "╠" + "─".repeat(54) + "╣" + RESET);

        for (ItemPedido item : pedido.getItens()) {
            String desc = String.format("  %dx %-24s Kz %8.2f",
                    item.getQuantidade(), item.getProduto().getNome(), item.calcularSubtotal());
            int pad = 54 - desc.length();
            System.out.println("  " + CIANO + BOLD + "║" + RESET
                    + desc + " ".repeat(Math.max(0, pad))
                    + CIANO + BOLD + "║" + RESET);
        }

        System.out.println("  " + CIANO + "╠" + "═".repeat(54) + "╣" + RESET);

        String totalLabel = "  TOTAL A PAGAR:";
        String totalValor = String.format("Kz %,.2f", pedido.calcularTotal());
        int espTotal = 54 - totalLabel.length() - totalValor.length();
        System.out.println("  " + CIANO + BOLD + "║" + RESET
                + BOLD + totalLabel + " ".repeat(Math.max(1, espTotal)) + RESET
                + VERDE + BOLD + totalValor + RESET
                + CIANO + BOLD + "║" + RESET);

        System.out.println("  " + CIANO + BOLD + "╠" + "═".repeat(54) + "╣" + RESET);
        System.out.println("  " + CIANO + BOLD + "║" + RESET
                + centrar(AMAR + "Obrigado pela preferência! Volte sempre!" + RESET, 54 + 10)
                + CIANO + BOLD + "║" + RESET);
        System.out.println("  " + CIANO + BOLD + "╚" + "═".repeat(54) + "╝" + RESET);
        System.out.println();
    }

    private String centrar(String texto, int largura) {
        int visivel = texto.replaceAll("\033\\[[;\\d]*m", "").length();
        int pad = (largura - visivel) / 2;
        return " ".repeat(Math.max(0, pad)) + texto + " ".repeat(Math.max(0, largura - visivel - pad));
    }

    private Pedido selecionarPedidoAberto() {
        List<Pedido> abertos = pedidoRepositorio.listarAbertos();
        if (abertos.isEmpty()) {
            Formatador.aviso("Não há pedidos em aberto. Cria um pedido primeiro.");
            return null;
        }
        System.out.println();
        System.out.println(Formatador.subtitulo("Pedidos em aberto"));
        System.out.println();
        for (Pedido p : abertos) {
            System.out.println(p.resumo());
        }
        Formatador.prompt("Número do pedido (0 para cancelar):");
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
