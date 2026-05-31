package com.cantina.controller;

import com.cantina.modelo.Venda;
import com.cantina.repositorio.UtilizadorRepositorio;
import com.cantina.servico.VendaServico;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('GERENTE')")
public class RelatorioController {

    private final VendaServico          vendaServico;
    private final UtilizadorRepositorio utilizadorRepo;

    public RelatorioController(VendaServico vendaServico, UtilizadorRepositorio utilizadorRepo) {
        this.vendaServico   = vendaServico;
        this.utilizadorRepo = utilizadorRepo;
    }

    @GetMapping("/relatorios")
    public String relatorios(Authentication auth, Model model) {
        model.addAttribute("nomeUtilizador", auth.getName());

        List<Venda> fechadas = vendaServico.listarTodas().stream()
            .filter(Venda::isFechada)
            .collect(Collectors.toList());

        Map<String, Long> porFuncionario = fechadas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getNomeFuncionario() != null ? v.getNomeFuncionario() : "—",
                LinkedHashMap::new,
                Collectors.counting()));

        Map<String, BigDecimal> receitaPorFunc = fechadas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getNomeFuncionario() != null ? v.getNomeFuncionario() : "—",
                Collectors.reducing(BigDecimal.ZERO, Venda::calcularTotal, BigDecimal::add)));

        Map<String, Long> porProduto = new HashMap<>();
        Map<String, BigDecimal> receitaPorProduto = new HashMap<>();
        fechadas.forEach(v -> v.getItens().forEach(item -> {
            String nome = item.getProduto().getNome();
            porProduto.merge(nome, (long) item.getQuantidade(), Long::sum);
            receitaPorProduto.merge(nome, item.calcularSubtotal(), BigDecimal::add);
        }));

        List<Map.Entry<String, Long>> ranking = porProduto.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());

        BigDecimal receitaTotal = fechadas.stream()
            .map(Venda::calcularTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalVendas",      fechadas.size());
        model.addAttribute("receitaTotal",     receitaTotal.toPlainString());
        model.addAttribute("porFuncionario",   porFuncionario);
        model.addAttribute("receitaPorFunc",   receitaPorFunc);
        model.addAttribute("rankingProdutos",  ranking);
        model.addAttribute("receitaPorProduto", receitaPorProduto);
        return "relatorios";
    }
}
