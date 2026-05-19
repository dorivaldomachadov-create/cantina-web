package com.cantina.controller;

import com.cantina.modelo.Produto;
import com.cantina.servico.EstoqueServico;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/produtos")
public class EstoqueController {
    private final EstoqueServico estoqueServico;
    public EstoqueController(EstoqueServico estoqueServico){
        this.estoqueServico = estoqueServico;
    }
    @GetMapping
    public List<Produto> ListarProdutos(){
        return estoqueServico.Listarprodutos();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Produto> BuscarProduto(@PathVariable Integer id){
        return estoqueServico.BuscarPorId(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Produto CriarProduto(@RequestBody Produto produto){
        return estoqueServico.SalvarProduto(produto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> DeletarPorId(@PathVariable Integer id){
        estoqueServico.DeletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}
