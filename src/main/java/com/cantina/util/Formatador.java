package cantina.util;

public class Formatador {

    private static final int LARGURA = 60;

    private static final String RESET   = "\033[0m";
    private static final String BOLD    = "\033[1m";
    private static final String DIM     = "\033[2m";

    private static final String VERDE   = "\033[38;5;82m";
    private static final String VERMELHO= "\033[38;5;196m";
    private static final String AMARELO = "\033[38;5;220m";
    private static final String AZUL    = "\033[38;5;75m";
    private static final String CIANO   = "\033[38;5;51m";
    private static final String LARANJA = "\033[38;5;214m";
    private static final String ROXO    = "\033[38;5;177m";

    private static final String BG_ESCURO = "\033[48;5;235m";

    public static String linha() {
        return DIM + "─".repeat(LARGURA) + RESET;
    }

    public static String linhaDouble() {
        return CIANO + BOLD + "═".repeat(LARGURA) + RESET;
    }

    public static String linhaMenor() {
        return DIM + "┄".repeat(LARGURA) + RESET;
    }

    public static String titulo(String texto) {
        String limpo = texto.strip();
        int total = LARGURA - 2;
        int pad = (total - limpo.length()) / 2;
        String espaco = " ".repeat(Math.max(0, pad));
        String linha = CIANO + BOLD + "╔" + "═".repeat(LARGURA - 2) + "╗" + RESET;
        String meio  = CIANO + BOLD + "║" + RESET
                     + espaco + LARANJA + BOLD + limpo + RESET
                     + espaco + (((total - limpo.length()) % 2 != 0) ? " " : "")
                     + CIANO + BOLD + "║" + RESET;
        String base  = CIANO + BOLD + "╚" + "═".repeat(LARGURA - 2) + "╝" + RESET;
        return "\n" + linha + "\n" + meio + "\n" + base;
    }

    public static String subtitulo(String texto) {
        String limpo = texto.strip();
        return AZUL + BOLD + "┌─ " + RESET + BOLD + limpo + RESET + "\n" + AZUL + "└" + "─".repeat(LARGURA - 1) + RESET;
    }

    public static String secao(String texto) {
        return "\n" + ROXO + BOLD + "  ▸ " + texto.toUpperCase() + RESET + "\n" + DIM + "  " + "╌".repeat(LARGURA - 2) + RESET;
    }

    public static String moeda(double valor) {
        return VERDE + BOLD + String.format("Kz %,.2f", valor) + RESET;
    }

    public static String moedaPlano(double valor) {
        return String.format("Kz %,.2f", valor);
    }

    public static String linhaFactura(String descricao, double valor) {
        String valorStr = moedaPlano(valor);
        int espacos = LARGURA - descricao.length() - valorStr.length();
        if (espacos < 1) espacos = 1;
        return BOLD + descricao + RESET + " ".repeat(espacos) + VERDE + BOLD + valorStr + RESET;
    }

    public static void erro(String msg) {
        System.out.println("\n  " + VERMELHO + BOLD + "✖  " + msg + RESET);
    }

    public static void sucesso(String msg) {
        System.out.println("\n  " + VERDE + BOLD + "✔  " + msg + RESET);
    }

    public static void aviso(String msg) {
        System.out.println("\n  " + AMARELO + BOLD + "⚠  " + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println("  " + AZUL + "ℹ  " + RESET + msg);
    }

    public static String tag(String texto, String cor) {
        return cor + BOLD + "[" + texto + "]" + RESET;
    }

    public static String tagVerde(String t)  { return tag(t, VERDE); }
    public static String tagAmarelo(String t){ return tag(t, AMARELO); }
    public static String tagVermelho(String t){ return tag(t, VERMELHO); }
    public static String tagCiano(String t)  { return tag(t, CIANO); }

    public static void prompt(String msg) {
        System.out.print("\n  " + CIANO + "❯ " + RESET + msg + " ");
    }

    public static void opcaoMenu(String num, String label, String detalhe) {
        System.out.println("  " + AZUL + BOLD + " " + num + " " + RESET
                + "  " + BOLD + label + RESET
                + DIM + "  " + detalhe + RESET);
    }

    public static void espaco() {
        System.out.println();
    }
}
