package com.cantina.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        boolean isGerente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE"));
        
        if (isGerente) {
            return "dashboard-gerente";
        }
        return "dashboard-funcionario";
    }
}
