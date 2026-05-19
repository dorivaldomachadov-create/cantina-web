//package cantina.repositorio;
//
//import cantina.modelo.Pedido;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Repositório de pedidos — guarda todos os pedidos em memória.
// */
//public class PedidoRepositorio {
//
//    private List<Pedido> pedidos;
//    private int proximoNumero;
//
//    public PedidoRepositorio() {
//        pedidos = new ArrayList<>();
//        proximoNumero = 1;
//    }
//
//    public Pedido criar(String nomeCliente) {
//        Pedido pedido = new Pedido(proximoNumero++, nomeCliente);
//        pedidos.add(pedido);
//        return pedido;
//    }
//
//    public Pedido buscarPorNumero(int numero) {
//        for (Pedido p : pedidos) {
//            if (p.getNumero() == numero) return p;
//        }
//        return null;
//    }
//
//    public List<Pedido> listarAbertos() {
//        List<Pedido> abertos = new ArrayList<>();
//        for (Pedido p : pedidos) {
//            if (p.isAberto()) abertos.add(p);
//        }
//        return abertos;
//    }
//
//    public List<Pedido> listarFechados() {
//        List<Pedido> fechados = new ArrayList<>();
//        for (Pedido p : pedidos) {
//            if (!p.isAberto()) fechados.add(p);
//        }
//        return fechados;
//    }
//
//    public List<Pedido> listarTodos() {
//        return new ArrayList<>(pedidos);
//    }
//
//    public int contarAbertos() {
//        int count = 0;
//        for (Pedido p : pedidos) if (p.isAberto()) count++;
//        return count;
//    }
//}
