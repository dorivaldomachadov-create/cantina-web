package com.cantina.controller;

import com.cantina.servico.RecuperacaoServico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final RecuperacaoServico recuperacaoServico;

    public AuthController(RecuperacaoServico recuperacaoServico) {
        this.recuperacaoServico = recuperacaoServico;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required=false) String erro,
                        @RequestParam(required=false) String saiu,
                        Model model) {
        if (erro  != null) model.addAttribute("mensagemErro", "Utilizador ou password incorretos.");
        if (saiu  != null) model.addAttribute("mensagemSaiu", "Sessão encerrada com sucesso.");
        return "login";
    }

    @GetMapping("/auth/recuperar")
    public String paginaRecuperar() { return "auth/recuperar"; }

    @PostMapping("/auth/recuperar")
    public String processarRecuperar(@RequestParam String email, RedirectAttributes ra) {
        String token = recuperacaoServico.gerarToken(email);
        if (token == null) {
            ra.addFlashAttribute("erro", "Nenhuma conta encontrada com esse email.");
            return "redirect:/auth/recuperar";
        }
        ra.addFlashAttribute("sucesso", "Token gerado: " + token + " (em produção seria enviado por email)");
        return "redirect:/auth/nova-senha?token=" + token;
    }

    @GetMapping("/auth/nova-senha")
    public String paginaNovaSenha(@RequestParam String token, Model model) {
        if (!recuperacaoServico.tokenValido(token)) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "auth/recuperar";
        }
        model.addAttribute("token", token);
        return "auth/nova-senha";
    }

    @PostMapping("/auth/nova-senha")
    public String processarNovaSenha(@RequestParam String token,
                                     @RequestParam String novaSenha,
                                     @RequestParam String confirmar,
                                     RedirectAttributes ra) {
        if (!novaSenha.equals(confirmar)) {
            ra.addFlashAttribute("erro", "As passwords não coincidem.");
            return "redirect:/auth/nova-senha?token=" + token;
        }
        if (!recuperacaoServico.redefinir(token, novaSenha)) {
            ra.addFlashAttribute("erro", "Token inválido ou expirado.");
            return "redirect:/auth/recuperar";
        }
        ra.addFlashAttribute("mensagemSaiu", "Password alterada com sucesso. Podes fazer login.");
        return "redirect:/login";
    }
}
