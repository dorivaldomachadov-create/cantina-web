package com.cantina.controller;

import com.cantina.modelo.Venda;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.repositorio.UtilizadorRepositorio;
import com.cantina.servico.VendaServico;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final VendaServico          vendaServico;
    private final ProdutoRepositorio    produtoRepo;
    private final UtilizadorRepositorio utilizadorRepo;

    public VendaController(VendaServico vendaServico, ProdutoRepositorio produtoRepo,
                           UtilizadorRepositorio utilizadorRepo) {
        this.vendaServico   = vendaServico;
        this.produtoRepo    = produtoRepo;
        this.utilizadorRepo = utilizadorRepo;
    }

    private void preencherSidebar(Model model, Authentication auth) {
        model.addAttribute("nomeUtilizador", auth.getName());
        utilizadorRepo.findByUsername(auth.getName())
            .ifPresent(u -> model.addAttribute("utilizador", u));
    }

    @GetMapping("/nova")
    public String abrirCaixa(Model model, Authentication auth) {
        preencherSidebar(model, auth);
        model.addAttribute("produtos", produtoRepo.findByAtivoTrue());
        model.addAttribute("venda", null);
        return "vendas/nova";
    }

    @PostMapping("/nova")
    public String iniciarNovaVenda(@RequestParam(required = false) String nomeCliente,
                                   Authentication auth, Model model,
                                   RedirectAttributes ra) {
        try {
            String nomeFuncionario = utilizadorRepo.findByUsername(auth.getName())
                .map(u -> u.getNome()).orElse(auth.getName());
            Venda novaVenda = vendaServico.iniciarNovaVenda(nomeCliente, nomeFuncionario);
            return "redirect:/vendas/" + novaVenda.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao iniciar venda: " + e.getMessage());
            return "redirect:/vendas/nova";
        }
    }

    @GetMapping("/{id}")
    public String verVenda(@PathVariable Integer id, Authentication auth, Model model) {
        Venda venda = vendaServico.buscarPorId(id);
        if (venda == null) return "redirect:/vendas/nova";
        preencherSidebar(model, auth);
        model.addAttribute("venda",    venda);
        model.addAttribute("produtos", produtoRepo.findByAtivoTrue());
        return "vendas/nova";
    }

    @PostMapping("/{id}/item")
    public String adicionarItem(@PathVariable Integer id,
                                @RequestParam(required = false) Integer idProduto,
                                @RequestParam(required = false) String nomeProduto,
                                @RequestParam(defaultValue = "1") int quantidade,
                                Authentication auth, Model model) {
        try {
            Venda v = vendaServico.adicionarProdutoAVenda(id, idProduto, nomeProduto, quantidade);
            model.addAttribute("venda", v);
        } catch (Exception e) {
            model.addAttribute("erroBusca", e.getMessage());
            model.addAttribute("venda", vendaServico.buscarPorId(id));
        }
        preencherSidebar(model, auth);
        model.addAttribute("produtos", produtoRepo.findByAtivoTrue());
        return "vendas/nova";
    }

    @PostMapping("/{id}/remover")
    public String removerItem(@PathVariable Integer id,
                              @RequestParam Integer idProduto,
                              Authentication auth, Model model) {
        try {
            model.addAttribute("venda", vendaServico.removerProdutoDaVenda(id, idProduto));
        } catch (Exception e) {
            model.addAttribute("erro",  e.getMessage());
            model.addAttribute("venda", vendaServico.buscarPorId(id));
        }
        preencherSidebar(model, auth);
        model.addAttribute("produtos", produtoRepo.findByAtivoTrue());
        return "vendas/nova";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarVenda(@PathVariable Integer id, RedirectAttributes ra) {
        Venda v = vendaServico.buscarPorId(id);
        if (v == null) {
            ra.addFlashAttribute("erro", "Venda não encontrada.");
            return "redirect:/vendas/nova";
        }
        if (v.getItens().isEmpty()) {
            ra.addFlashAttribute("erro", "Não é possível finalizar uma venda sem itens.");
            return "redirect:/vendas/" + id;
        }
        vendaServico.fecharVenda(id);
        return "redirect:/vendas/" + id + "/fatura";
    }

    @GetMapping("/{id}/fatura")
    public String verFatura(@PathVariable Integer id, Authentication auth, Model model) {
        Venda venda = vendaServico.buscarPorId(id);
        if (venda == null) return "redirect:/dashboard";
        preencherSidebar(model, auth);
        model.addAttribute("venda", venda);
        return "vendas/fatura";
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('GERENTE')")
    public String cancelarVenda(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            vendaServico.cancelarVenda(id);
            ra.addFlashAttribute("sucesso", "Venda cancelada e stock devolvido.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/vendas/historico";
    }

    @GetMapping("/historico")
    @PreAuthorize("hasRole('GERENTE')")
    public String historico(@RequestParam(required = false) String estado,
                            @RequestParam(required = false) String funcionario,
                            Authentication auth, Model model) {
        preencherSidebar(model, auth);
        List<Venda> filtradas = vendaServico.listarTodas().stream()
            .filter(v -> estado == null || estado.isBlank() || v.getEstado().name().equals(estado))
            .filter(v -> funcionario == null || funcionario.isBlank() ||
                (v.getNomeFuncionario() != null &&
                 v.getNomeFuncionario().toLowerCase().contains(funcionario.toLowerCase())))
            .filter(v -> v.getId() != null)
            .sorted((a, b) -> b.getId().compareTo(a.getId()))
            .collect(Collectors.toList());

        model.addAttribute("vendas",            filtradas);
        model.addAttribute("totalVendas",       filtradas.size());
        model.addAttribute("receitaTotal",      filtradas.stream().filter(Venda::isFechada)
            .map(Venda::calcularTotal).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add).toPlainString());
        model.addAttribute("vendasCanceladas",  filtradas.stream().filter(Venda::isCancelada).count());
        model.addAttribute("filtroEstado",      estado);
        model.addAttribute("filtroFuncionario", funcionario);
        return "vendas/historico";
    }
}
