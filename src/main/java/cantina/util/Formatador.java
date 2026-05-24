package cantina.util;

/**
 * Classe utilitária com métodos estáticos para formatar
 * textos e valores no terminal.
 */
public class Formatador {

    private static final int LARGURA = 55;

    // Linha decorativa simples
    public static String linha() {
        return "=".repeat(LARGURA);
    }

    // Linha decorativa menor
    public static String linhaMenor() {
        return "-".repeat(LARGURA);
    }

    // Título centrado dentro de uma caixa
    public static String titulo(String texto) {
        int espacos = (LARGURA - texto.length()) / 2;
        String pad = " ".repeat(Math.max(0, espacos));
        return linha() + "\n" + pad + texto + "\n" + linha();
    }

    // Subtítulo
    public static String subtitulo(String texto) {
        return linhaMenor() + "\n  " + texto + "\n" + linhaMenor();
    }

    // Formata moeda angolana
    public static String moeda(double valor) {
        return String.format("Kz %,.2f", valor);
    }

    // Linha de item de factura (nome à esquerda, valor à direita)
    public static String linhaFactura(String descricao, double valor) {
        String valorStr = moeda(valor);
        int espacos = LARGURA - descricao.length() - valorStr.length();
        if (espacos < 1) espacos = 1;
        return descricao + " ".repeat(espacos) + valorStr;
    }

    // Imprime uma linha em branco
    public static void espaco() {
        System.out.println();
    }

    // Imprime mensagem de erro formatada
    public static void erro(String msg) {
        System.out.println("\n  ❌ ERRO: " + msg);
    }

    // Imprime mensagem de sucesso formatada
    public static void sucesso(String msg) {
        System.out.println("\n  ✅ " + msg);
    }

    // Imprime aviso formatado
    public static void aviso(String msg) {
        System.out.println("\nATENÇÃO: " + msg);
    }
}
