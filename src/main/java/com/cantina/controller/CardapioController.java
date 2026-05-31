package com.cantina.controller;

import com.cantina.modelo.Produto;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.repositorio.UtilizadorRepositorio;
import com.cantina.servico.EstoqueServico;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cardapio")
@PreAuthorize("hasRole('GERENTE')")
public class CardapioController {

    private final ProdutoRepositorio    produtoRepo;
    private final UtilizadorRepositorio utilizadorRepo;
    private final EstoqueServico        estoqueServico;

    public CardapioController(ProdutoRepositorio produtoRepo,
                               UtilizadorRepositorio utilizadorRepo,
                               EstoqueServico estoqueServico) {
        this.produtoRepo    = produtoRepo;
        this.utilizadorRepo = utilizadorRepo;
        this.estoqueServico = estoqueServico;
    }

    private void sidebar(Model model, Authentication auth) {
        model.addAttribute("nomeUtilizador", auth.getName());
        utilizadorRepo.findByUsername(auth.getName())
            .ifPresent(u -> model.addAttribute("utilizador", u));
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {
        sidebar(model, auth);
        model.addAttribute("produtos", produtoRepo.findAll());
        return "cardapio/index";
    }

    @GetMapping("/novo")
    public String novoPagina(Authentication auth, Model model) {
        sidebar(model, auth);
        return "cardapio/form";
    }

    @PostMapping("/novo")
    public String criar(@RequestParam String nome,
                        @RequestParam BigDecimal preco,
                        @RequestParam int estoque,
                        @RequestParam String categoria,
                        RedirectAttributes ra) {
        if (nome == null || nome.isBlank()) {
            ra.addFlashAttribute("erro", "Nome inválido.");
            return "redirect:/cardapio/novo";
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            ra.addFlashAttribute("erro", "Preço inválido.");
            return "redirect:/cardapio/novo";
        }
        Produto p = new Produto();
        p.setNome(nome.trim());
        p.setPreco(preco);
        p.setQuantidadeEstoque(estoque);
        p.setCategoria(categoria);
        p.setAtivo(true);
        produtoRepo.save(p);
        ra.addFlashAttribute("sucesso", "\"" + nome.trim() + "\" adicionado ao cardápio.");
        return "redirect:/cardapio";
    }

    @GetMapping("/editar/{id}")
    public String editarPagina(@PathVariable Integer id, Authentication auth, Model model) {
        return produtoRepo.findById(id).map(p -> {
            sidebar(model, auth);
            model.addAttribute("produto", p);
            return "cardapio/form";
        }).orElse("redirect:/cardapio");
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         @RequestParam String nome,
                         @RequestParam BigDecimal preco,
                         @RequestParam String categoria,
                         RedirectAttributes ra) {
        produtoRepo.findById(id).ifPresent(p -> {
            p.setNome(nome.trim());
            p.setPreco(preco);
            p.setCategoria(categoria);
            produtoRepo.save(p);
        });
        ra.addFlashAttribute("sucesso", "Produto atualizado.");
        return "redirect:/cardapio";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Integer id, RedirectAttributes ra) {
        estoqueServico.toggleAtivo(id);
        ra.addFlashAttribute("sucesso", "Estado do produto alterado.");
        return "redirect:/cardapio";
    }
}
