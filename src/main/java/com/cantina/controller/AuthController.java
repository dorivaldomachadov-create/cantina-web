package com.cantina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String paginaLogin(
            @RequestParam(value = "erro",  required = false) String erro,
            @RequestParam(value = "saiu",  required = false) String saiu,
            Model model) {

        if (erro != null) {
            model.addAttribute("mensagemErro", "Utilizador ou password incorretos.");
        }
        if (saiu != null) {
            model.addAttribute("mensagemSaiu", "Sessão encerrada com sucesso.");
        }

        return "login";
    }
}
