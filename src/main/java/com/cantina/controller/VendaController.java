package com.cantina.controller;

import com.cantina.modelo.Venda;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.servico.VendaServico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final VendaServico vendaServico;
    private final ProdutoRepositorio produtoRepositorio;

    public VendaController(VendaServico vendaServico, ProdutoRepositorio produtoRepositorio) {
        this.vendaServico = vendaServico;
        this.produtoRepositorio = produtoRepositorio;
    }

    @GetMapping("/nova")
    public String abrirCaixa(Model model) {
        model.addAttribute("produtos", produtoRepositorio.findAll());
        model.addAttribute("venda", null); // Inicia a tela sem nenhuma venda ativa
        return "vendas/nova";
    }

    // Cria a venda em aberto (Acaba com o NullPointerException!)
    @PostMapping("/nova")
    public String iniciarNovaVenda(@RequestParam(required = false) String nomeCliente, Model model) {
        try {
            Venda novaVenda = vendaServico.iniciarNovaVenda(nomeCliente);
            model.addAttribute("venda", novaVenda);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao iniciar a venda: " + e.getMessage());
        }
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "vendas/nova";
    }

    // Adiciona o item à venda atual
    @PostMapping("/{id}/item")
    public String adicionarItem(@PathVariable Integer id,
                                @RequestParam(required = false) Integer idProduto,
                                @RequestParam int quantidade,
                                Model model) {
        try {
            if (idProduto == null) {
                throw new IllegalArgumentException("Insira um ID de produto válido.");
            }
            Venda vendaAtualizada = vendaServico.adicionarProdutoAVenda(id, idProduto, quantidade);
            model.addAttribute("venda", vendaAtualizada);
        } catch (Exception e) {
            model.addAttribute("erroBusca", e.getMessage());
            model.addAttribute("venda", vendaServico.buscarPorId(id)); // Mantém a venda na tela
        }
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "vendas/nova";
    }

    // Remove o item da venda atual
    @PostMapping("/{id}/remover")
    public String removerItem(@PathVariable Integer id, @RequestParam Integer idProduto, Model model) {
        try {
            Venda vendaAtualizada = vendaServico.removerProdutoDaVenda(id, idProduto);
            model.addAttribute("venda", vendaAtualizada);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao remover item: " + e.getMessage());
            model.addAttribute("venda", vendaServico.buscarPorId(id));
        }
        model.addAttribute("produtos", produtoRepositorio.findAll());
        return "vendas/nova";

    }

    // Rota para finalizar a venda e abrir a Fatura
    @PostMapping("/{id}/finalizar")
    public String finalizarVenda(@PathVariable Integer id, Model model) {
        try {
            //  redireciona dinamicamente para a rota da fatura com o ID certo!
            return "redirect:/vendas/" + id + "/fatura";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao finalizar a venda: " + e.getMessage());
            model.addAttribute("venda", vendaServico.buscarPorId(id));
            model.addAttribute("produtos", produtoRepositorio.findAll());
            return "vendas/nova";
        }
    }

    @GetMapping("/{id}/fatura")
    public String exibirFatura(@PathVariable Integer id, Model model) {
        Venda venda = vendaServico.buscarPorId(id);
        if (venda == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("venda", venda);
        return "vendas/fatura"; // abrir o arquivo fatura.html
    }

    @GetMapping("/historico")
    public String exibirHistorico(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String funcionario,
            Model model) {

        List<Venda> vendas = vendaServico.listarTodas();

        //  Calcula as estatísticas de forma segura
        long totalVendas = vendas.size();

        double receitaTotal = vendas.stream()
                .mapToDouble(Venda::calcularTotal)
                .sum();

        long vendasCanceladas = 0; // Fica a zero porque a classe Venda não tem estado de cancelamento

        //  Envia os dados para o Thymeleaf
        model.addAttribute("vendas", vendas);
        model.addAttribute("totalVendas", totalVendas);
        model.addAttribute("receitaTotal", receitaTotal);
        model.addAttribute("vendasCanceladas", vendasCanceladas);

        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("filtroFuncionario", funcionario);
        model.addAttribute("nomeUtilizador", "Gerente");

        return "vendas/historico";
    }

}


