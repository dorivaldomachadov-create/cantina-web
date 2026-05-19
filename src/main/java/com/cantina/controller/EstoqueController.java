package com.cantina.controller;

import com.cantina.modelo.Produto;
import com.cantina.servico.EstoqueServico;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EstoqueController {

    private final EstoqueServico estoqueServico;

    public EstoqueController(EstoqueServico estoqueServico) {
        this.estoqueServico = estoqueServico;
    }

    // ── Utilitário: preenche os atributos comuns no model ──────────────────
    private void preencherModel(Model model, Authentication auth) {
        List<Produto> produtos = estoqueServico.listarProdutos();
        model.addAttribute("produtos", produtos);
        model.addAttribute("totalProdutos", produtos.size());
        model.addAttribute("produtosOk",  estoqueServico.contarProdutosOk());
        model.addAttribute("stockBaixo",  estoqueServico.contarStockBaixo());
        model.addAttribute("semStock",    estoqueServico.contarSemStock());
        model.addAttribute("nomeUtilizador", auth.getName());
        model.addAttribute("perfil", auth.getAuthorities().iterator().next().getAuthority());
    }

    // ── GET /estoque ────────────────────────────────────────────────────────
    @GetMapping("/estoque")
    public String mostrarEstoque(Model model, Authentication auth) {
        preencherModel(model, auth);
        return "estoque";
    }

    // ── GET /estoque/repor?id=X ─────────────────────────────────────────────
    @GetMapping("/estoque/repor")
    public String mostrarRepor(@RequestParam(required = false) Integer id,
                               Model model, Authentication auth) {
        preencherModel(model, auth);
        if (id != null) {
            estoqueServico.buscarPorId(id).ifPresent(p -> {
                model.addAttribute("reporId",   p.getId());
                model.addAttribute("reporNome", p.getNome());
            });
        }
        return "estoque";
    }

    // ── POST /estoque/repor ─────────────────────────────────────────────────
    @PostMapping("/estoque/repor")
    public String processarRepor(@RequestParam Integer id,
                                 @RequestParam int quantidade,
                                 RedirectAttributes redirectAttrs) {
        estoqueServico.reporStock(id, quantidade);
        redirectAttrs.addFlashAttribute("sucesso", "Stock reposto com sucesso!");
        return "redirect:/estoque";
    }

    // ── GET /estoque/novo ───────────────────────────────────────────────────
    @GetMapping("/estoque/novo")
    public String mostrarFormulario(Model model, Authentication auth) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("nomeUtilizador", auth.getName());
        model.addAttribute("perfil", auth.getAuthorities().iterator().next().getAuthority());
        return "novo-produto";
    }

    // ── POST /estoque/novo ──────────────────────────────────────────────────
    @PostMapping("/estoque/novo")
    public String cadastrarProduto(@ModelAttribute Produto produto,
                                   RedirectAttributes redirectAttrs) {
        estoqueServico.salvarProduto(produto);
        redirectAttrs.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
        return "redirect:/estoque";
    }

    // ── GET /estoque/apagar/{id} ────────────────────────────────────────────
    @GetMapping("/estoque/apagar/{id}")
    public String apagarProduto(@PathVariable Integer id,
                                RedirectAttributes redirectAttrs) {
        estoqueServico.deletarProduto(id);
        redirectAttrs.addFlashAttribute("sucesso", "Produto removido com sucesso!");
        return "redirect:/estoque";
    }
}