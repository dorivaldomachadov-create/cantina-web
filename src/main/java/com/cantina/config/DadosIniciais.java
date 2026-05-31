package com.cantina.config;

import com.cantina.modelo.Produto;
import com.cantina.modelo.Utilizador;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.repositorio.UtilizadorRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DadosIniciais {

    @Bean
    public CommandLineRunner carregarDados(UtilizadorRepositorio utilizadorRepo,
                                           ProdutoRepositorio produtoRepo,
                                           PasswordEncoder encoder) {
        return args -> {
            if (utilizadorRepo.count() == 0) {
                utilizadorRepo.save(new Utilizador("Gerente Principal", "gerente",
                    encoder.encode("admin"), "gerente@cantina.com",
                    "+244 923 000 001", "Gerente Geral", Utilizador.Perfil.GERENTE));
                utilizadorRepo.save(new Utilizador("Funcionário Demo", "funcionario",
                    encoder.encode("1234"), "funcionario@cantina.com",
                    "+244 923 000 002", "Operador de Caixa", Utilizador.Perfil.FUNCIONARIO));
                System.out.println("[Cantina] Utilizadores criados.");
            }
            if (produtoRepo.count() == 0) {
                Object[][] ps = {
                    {"Frango Grelhado",    new BigDecimal("1200"), 15, "Comida"},
                    {"Feijoada",           new BigDecimal("900"),  12, "Comida"},
                    {"Pão com Manteiga",   new BigDecimal("150"),  30, "Comida"},
                    {"Sanduíche de Frango",new BigDecimal("500"),  20, "Comida"},
                    {"Arroz com Feijão",   new BigDecimal("700"),  18, "Comida"},
                    {"Salada de Legumes",  new BigDecimal("400"),  10, "Comida"},
                    {"Água Mineral 500ml", new BigDecimal("100"),  50, "Bebida"},
                    {"Sumo de Laranja",    new BigDecimal("200"),  25, "Bebida"},
                    {"Refrigerante Lata",  new BigDecimal("250"),  30, "Bebida"},
                    {"Sumo de Manga",      new BigDecimal("250"),  20, "Bebida"},
                    {"Café Expresso",      new BigDecimal("150"),  40, "Bebida"},
                    {"Chá Natural",        new BigDecimal("120"),   3, "Bebida"},
                    {"Biscoitos Pack",     new BigDecimal("200"),   3, "Snack"},
                    {"Batata Frita Pack",  new BigDecimal("300"),   8, "Snack"},
                };
                for (Object[] d : ps) {
                    Produto p = new Produto();
                    p.setNome((String) d[0]);
                    p.setPreco((BigDecimal) d[1]);
                    p.setQuantidadeEstoque((int) d[2]);
                    p.setCategoria((String) d[3]);
                    p.setAtivo(true);
                    produtoRepo.save(p);
                }
                System.out.println("[Cantina] Produtos criados.");
            }
        };
    }
}
