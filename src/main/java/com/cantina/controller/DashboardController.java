package com.cantina.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String nome = auth.getName();
        model.addAttribute("nomeUtilizador", nome);

        boolean eGerente = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE"));

        if (eGerente) {
            return "dashboard-gerente";
        }
        return "dashboard-funcionario";
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }
}
