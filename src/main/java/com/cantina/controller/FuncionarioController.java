package com.cantina.controller;

import com.cantina.modelo.Utilizador;
import com.cantina.repositorio.UtilizadorRepositorio;
import com.cantina.servico.FuncionarioServico;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/funcionarios")
@PreAuthorize("hasRole('GERENTE')")
public class FuncionarioController {

    private final FuncionarioServico servico;
    private final UtilizadorRepositorio repo;

    public FuncionarioController(FuncionarioServico servico, UtilizadorRepositorio repo) {
        this.servico = servico;
        this.repo    = repo;
    }

    private void sidebar(Model model, Authentication auth) {
        var u = repo.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("utilizador",     u);
        model.addAttribute("nomeUtilizador", auth.getName());
    }

    @GetMapping
    public String listar(Authentication auth, Model model) {
        sidebar(model, auth);
        model.addAttribute("funcionarios", servico.listarTodos());
        return "funcionarios/index";
    }

    @GetMapping("/novo")
    public String novoPagina(Authentication auth, Model model) {
        sidebar(model, auth);
        model.addAttribute("perfis", Utilizador.Perfil.values());
        return "funcionarios/form";
    }

    @PostMapping("/novo")
    public String criar(@RequestParam String nome, @RequestParam String username,
                        @RequestParam String password, @RequestParam String email,
                        @RequestParam(required=false) String telefone,
                        @RequestParam(required=false) String cargo,
                        @RequestParam Utilizador.Perfil perfil,
                        RedirectAttributes ra) {
        String erro = servico.criar(nome, username, password, email, telefone, cargo, perfil);
        if (erro != null) { ra.addFlashAttribute("erro", erro); return "redirect:/funcionarios/novo"; }
        ra.addFlashAttribute("sucesso", "Conta de \"" + nome + "\" criada.");
        return "redirect:/funcionarios";
    }

    @GetMapping("/editar/{id}")
    public String editarPagina(@PathVariable Integer id, Authentication auth, Model model) {
        var func = servico.buscarPorId(id);
        if (func == null) return "redirect:/funcionarios";
        sidebar(model, auth);
        model.addAttribute("func",   func);
        model.addAttribute("perfis", Utilizador.Perfil.values());
        return "funcionarios/editar";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @RequestParam String nome,
                         @RequestParam String email, @RequestParam(required=false) String telefone,
                         @RequestParam(required=false) String cargo,
                         @RequestParam Utilizador.Perfil perfil, RedirectAttributes ra) {
        String erro = servico.atualizar(id, nome, email, telefone, cargo, perfil);
        if (erro != null) ra.addFlashAttribute("erro", erro);
        else ra.addFlashAttribute("sucesso", "Dados atualizados.");
        return "redirect:/funcionarios";
    }

    @PostMapping("/senha/{id}")
    public String alterarSenha(@PathVariable Integer id, @RequestParam String novaSenha, RedirectAttributes ra) {
        String erro = servico.alterarPassword(id, novaSenha);
        if (erro != null) ra.addFlashAttribute("erro", erro);
        else ra.addFlashAttribute("sucesso", "Password alterada com sucesso.");
        return "redirect:/funcionarios";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Integer id, RedirectAttributes ra) {
        servico.toggleAtivo(id);
        ra.addFlashAttribute("sucesso", "Estado da conta alterado.");
        return "redirect:/funcionarios";
    }
}
