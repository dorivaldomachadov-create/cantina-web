//package cantina.servico;
//
//import cantina.modelo.Bebida;
//import cantina.modelo.Comida;
//import cantina.modelo.Produto;
//import cantina.repositorio.ProdutoRepositorio;
//import cantina.util.Formatador;
//
//import java.util.InputMismatchException;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Serviço responsável pela gestão do cardápio.
// * Criado pelo membro: Eduardo Luís
// */
//public class CardapioServico {
//
//    private ProdutoRepositorio repositorio;
//    private Scanner scanner;
//
//    public CardapioServico(ProdutoRepositorio repositorio, Scanner scanner) {
//        this.repositorio = repositorio;
//        this.scanner = scanner;
//    }
//
//    // ---------- Menus ----------
//
//    public void menuCardapio() {
//        int opcao = -1;
//        while (opcao != 0) {
//            System.out.println("\n" + Formatador.titulo("  GESTÃO DO CARDÁPIO  "));
//            System.out.println("  1. Ver cardápio completo");
//            System.out.println("  2. Ver apenas comidas");
//            System.out.println("  3. Ver apenas bebidas");
//            System.out.println("  4. Procurar produto por nome");
//            System.out.println("  5. Adicionar produto ao cardápio");
//            System.out.println("  6. Remover produto do cardápio");
//            System.out.println("  0. Voltar ao menu principal");
//            System.out.print("\n  Escolhe uma opção: ");
//
//            try {
//                opcao = scanner.nextInt();
//                scanner.nextLine();
//            } catch (InputMismatchException e) {
//                Formatador.erro("Introduz apenas um número.");
//                scanner.nextLine();
//                continue;
//            }
//
//            switch (opcao) {
//                case 1 -> mostrarCardapioCompleto();
//                case 2 -> mostrarPorCategoria("Comida");
//                case 3 -> mostrarPorCategoria("Bebida");
//                case 4 -> procurarPorNome();
//                case 5 -> adicionarProduto();
//                case 6 -> removerProduto();
//                case 0 -> System.out.println("  A voltar ao menu principal...");
//                default -> Formatador.erro("Opção inválida. Escolhe entre 0 e 6.");
//            }
//        }
//    }
//
//    // ---------- Operações ----------
//
//    public void mostrarCardapioCompleto() {
//        List<Produto> produtos = repositorio.listarTodos();
//        System.out.println("\n" + Formatador.titulo("  CARDÁPIO COMPLETO  "));
//        if (produtos.isEmpty()) {
//            System.out.println("  Cardápio vazio.");
//            return;
//        }
//        System.out.println("\n  === COMIDAS ===");
//        boolean temComida = false;
//        for (Produto p : produtos) {
//            if (p.getCategoria().equalsIgnoreCase("Comida")) {
//                System.out.println("  " + p.descricao());
//                temComida = true;
//            }
//        }
//        if (!temComida) System.out.println("  Sem comidas disponíveis.");
//
//        System.out.println("\n  === BEBIDAS ===");
//        boolean temBebida = false;
//        for (Produto p : produtos) {
//            if (p.getCategoria().equalsIgnoreCase("Bebida")) {
//                System.out.println("  " + p.descricao());
//                temBebida = true;
//            }
//        }
//        if (!temBebida) System.out.println("  Sem bebidas disponíveis.");
//
//        System.out.println("\n" + Formatador.linhaMenor());
//        System.out.println("  Total de produtos: " + produtos.size());
//    }
//
//    public void mostrarPorCategoria(String categoria) {
//        List<Produto> produtos = repositorio.listarPorCategoria(categoria);
//        System.out.println("\n" + Formatador.titulo("  " + categoria.toUpperCase() + "S  "));
//        if (produtos.isEmpty()) {
//            System.out.println("  Nenhum(a) " + categoria.toLowerCase() + " disponível.");
//            return;
//        }
//        for (Produto p : produtos) {
//            System.out.println("  " + p.descricao());
//        }
//    }
//
//    private void procurarPorNome() {
//        System.out.print("\n  Escreve o nome a procurar: ");
//        String nome = scanner.nextLine().trim();
//        if (nome.isEmpty()) { Formatador.erro("Nome vazio."); return; }
//        Produto p = repositorio.buscarPorNome(nome);
//        if (p == null) {
//            Formatador.erro("Nenhum produto encontrado com esse nome.");
//        } else {
//            System.out.println("\n  Produto encontrado:");
//            System.out.println("  " + p.descricao());
//        }
//    }
//
//    private void adicionarProduto() {
//        System.out.println("\n" + Formatador.subtitulo("  ADICIONAR PRODUTO  "));
//        try {
//            System.out.print("  Nome do produto: ");
//            String nome = scanner.nextLine().trim();
//            if (nome.isEmpty()) { Formatador.erro("Nome inválido."); return; }
//
//            System.out.print("  Preço (Kz): ");
//            double preco = scanner.nextDouble();
//            if (preco <= 0) { Formatador.erro("Preço tem de ser positivo."); scanner.nextLine(); return; }
//
//            System.out.print("  Quantidade em estoque: ");
//            int qtd = scanner.nextInt();
//            if (qtd < 0) { Formatador.erro("Quantidade inválida."); scanner.nextLine(); return; }
//
//            System.out.print("  Categoria (1-Comida / 2-Bebida): ");
//            int cat = scanner.nextInt();
//            scanner.nextLine();
//
//            if (cat == 1) {
//                System.out.print("  É vegetariana? (s/n): ");
//                boolean veg = scanner.nextLine().trim().equalsIgnoreCase("s");
//                repositorio.adicionar(new Comida(0, nome, preco, qtd, veg));
//            } else if (cat == 2) {
//                System.out.print("  É gelada? (s/n): ");
//                boolean gel = scanner.nextLine().trim().equalsIgnoreCase("s");
//                repositorio.adicionar(new Bebida(0, nome, preco, qtd, gel));
//            } else {
//                Formatador.erro("Categoria inválida.");
//                return;
//            }
//            Formatador.sucesso("Produto \"" + nome + "\" adicionado com sucesso!");
//
//        } catch (InputMismatchException e) {
//            Formatador.erro("Valor inválido introduzido.");
//            scanner.nextLine();
//        }
//    }
//
//    private void removerProduto() {
//        mostrarCardapioCompleto();
//        System.out.print("\n  ID do produto a remover (0 para cancelar): ");
//        try {
//            int id = scanner.nextInt();
//            scanner.nextLine();
//            if (id == 0) return;
//            Produto p = repositorio.buscarPorId(id);
//            if (p == null) { Formatador.erro("Produto não encontrado."); return; }
//            System.out.print("  Tens a certeza que queres remover \"" + p.getNome() + "\"? (s/n): ");
//            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
//                repositorio.remover(id);
//                Formatador.sucesso("Produto removido com sucesso.");
//            } else {
//                System.out.println("  Operação cancelada.");
//            }
//        } catch (InputMismatchException e) {
//            Formatador.erro("Introduz um número válido.");
//            scanner.nextLine();
//        }
//    }
//
//    // ---------- Acesso directo pelo repositório ----------
//
//    public Produto buscarPorId(int id) {
//        return repositorio.buscarPorId(id);
//    }
//
//    public List<Produto> listarTodos() {
//        return repositorio.listarTodos();
//    }
//}
