package com.cantina.controller;

import com.cantina.dto.VendaRequest;
import com.cantina.modelo.Venda;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.servico.VendaServico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("venda", null);
        return "vendas/nova";
    }

    @PostMapping("/nova")
    public String iniciarNovaVenda(@ModelAttribute VendaRequest vendaRequest, Model model) {
        try {
            // Agora passamos o DTO completo que o serviço espera
            Venda novaVenda = vendaServico.realizarVenda(vendaRequest);

            model.addAttribute("venda", novaVenda);
            model.addAttribute("produtos", produtoRepositorio.findAll());

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao iniciar a venda: " + e.getMessage());
        }

        return "vendas/nova";
    }
}



