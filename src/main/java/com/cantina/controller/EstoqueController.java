package com.cantina.controller;

import com.cantina.modelo.Produto;
import com.cantina.repositorio.UtilizadorRepositorio;
import com.cantina.servico.EstoqueServico;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class EstoqueController {

    private final EstoqueServico        estoqueServico;
    private final UtilizadorRepositorio utilizadorRepo;

    public EstoqueController(EstoqueServico estoqueServico, UtilizadorRepositorio utilizadorRepo) {
        this.estoqueServico = estoqueServico;
        this.utilizadorRepo = utilizadorRepo;
    }

    private void preencherModel(Model model, Authentication auth) {
        List<Produto> produtos = estoqueServico.listarProdutos();
        model.addAttribute("produtos",      produtos);
        model.addAttribute("totalProdutos", produtos.size());
        model.addAttribute("produtosOk",    estoqueServico.contarProdutosOk());
        model.addAttribute("stockBaixo",    estoqueServico.contarStockBaixo());
        model.addAttribute("semStock",      estoqueServico.contarSemStock());
        model.addAttribute("nomeUtilizador", auth.getName());
        utilizadorRepo.findByUsername(auth.getName())
            .ifPresent(u -> model.addAttribute("utilizador", u));
    }

    @GetMapping("/estoque")
    public String mostrarEstoque(Model model, Authentication auth) {
        preencherModel(model, auth);
        return "estoque";
    }

    @GetMapping("/estoque/repor")
    @PreAuthorize("hasRole('GERENTE')")
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

    @PostMapping("/estoque/repor")
    @PreAuthorize("hasRole('GERENTE')")
    public String processarRepor(@RequestParam Integer id,
                                 @RequestParam int quantidade,
                                 RedirectAttributes ra) {
        try {
            estoqueServico.reporStock(id, quantidade);
            ra.addFlashAttribute("sucesso", "Stock reposto com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/estoque";
    }

    @GetMapping("/estoque/novo")
    @PreAuthorize("hasRole('GERENTE')")
    public String mostrarFormulario(Model model, Authentication auth) {
        model.addAttribute("nomeUtilizador", auth.getName());
        utilizadorRepo.findByUsername(auth.getName())
            .ifPresent(u -> model.addAttribute("utilizador", u));
        model.addAttribute("produto", new Produto());
        return "novo-produto";
    }

    @PostMapping("/estoque/novo")
    @PreAuthorize("hasRole('GERENTE')")
    public String cadastrarProduto(@RequestParam String nome,
                                   @RequestParam BigDecimal preco,
                                   @RequestParam int quantidadeEstoque,
                                   @RequestParam String categoria,
                                   RedirectAttributes ra) {
        Produto produto = new Produto();
        produto.setNome(nome.trim());
        produto.setPreco(preco);
        produto.setQuantidadeEstoque(quantidadeEstoque);
        produto.setCategoria(categoria);
        produto.setAtivo(true);
        estoqueServico.salvarProduto(produto);
        ra.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
        return "redirect:/estoque";
    }

    @PostMapping("/estoque/apagar/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String apagarProduto(@PathVariable Integer id, RedirectAttributes ra) {
        estoqueServico.toggleAtivo(id);
        ra.addFlashAttribute("sucesso", "Estado do produto alterado.");
        return "redirect:/estoque";
    }
}
