package com.cantina.servico;

import com.cantina.modelo.Produto;
import com.cantina.repositorio.ProdutoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServico {

    private final ProdutoRepositorio produtoRepositorio;

    public EstoqueServico(ProdutoRepositorio produtoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
    }

    public List<Produto> listarProdutos() {
        return produtoRepositorio.findAll();
    }

    public Optional<Produto> buscarPorId(Integer id) {
        return produtoRepositorio.findById(id);
    }

    public Produto salvarProduto(Produto produto) {
        return produtoRepositorio.save(produto);
    }

    public void deletarProduto(Integer id) {
        produtoRepositorio.deleteById(id);
    }

    public void reporStock(Integer id, int quantidade) {
        produtoRepositorio.findById(id).ifPresent(p -> {
            p.setQuantidadeEstoque(p.getQuantidadeEstoque() + quantidade);
            produtoRepositorio.save(p);
        });
    }

    public long contarProdutosOk() {
        return produtoRepositorio.findByQuantidadeEstoqueGreaterThan(3).size();
    }

    public long contarStockBaixo() {
        return produtoRepositorio.findByQuantidadeEstoqueBetween(1, 3).size();
    }

    public long contarSemStock() {
        return produtoRepositorio.findByQuantidadeEstoque(0).size();
    }
}