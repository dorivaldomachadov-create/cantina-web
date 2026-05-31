package com.cantina.controller;

import com.cantina.repositorio.UtilizadorRepositorio;
import com.cantina.servico.EstoqueServico;
import com.cantina.servico.VendaServico;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final VendaServico          vendaServico;
    private final EstoqueServico        estoqueServico;
    private final UtilizadorRepositorio utilizadorRepo;

    public DashboardController(VendaServico vendaServico, EstoqueServico estoqueServico,
                                UtilizadorRepositorio utilizadorRepo) {
        this.vendaServico   = vendaServico;
        this.estoqueServico = estoqueServico;
        this.utilizadorRepo = utilizadorRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        utilizadorRepo.findByUsername(auth.getName()).ifPresent(u ->
            model.addAttribute("utilizador", u));

        model.addAttribute("nomeUtilizador", auth.getName());
        model.addAttribute("vendasHoje",     vendaServico.contarVendasHoje());
        model.addAttribute("receitaHoje",    vendaServico.receitaHoje().toPlainString());
        model.addAttribute("stockBaixo",     estoqueServico.contarStockBaixo());
        model.addAttribute("totalProdutos",  estoqueServico.listarProdutos().size());
        model.addAttribute("produtosStockBaixo",
            estoqueServico.listarProdutos().stream()
                .filter(p -> p.getQuantidadeEstoque() <= 5 && p.getQuantidadeEstoque() > 0)
                .limit(6).toList());
        model.addAttribute("ultimasVendas",
            vendaServico.listarTodas().stream()
                .filter(v -> v.getId() != null)
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(5).toList());

        boolean gerente = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE"));
        return gerente ? "dashboard-gerente" : "dashboard-funcionario";
    }

    @GetMapping("/")
    public String raiz(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) return "redirect:/dashboard";
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String inicio() { return "public/inicio"; }

    @GetMapping("/sobre")
    public String sobre()  { return "public/sobre"; }
}
