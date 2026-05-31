package com.cantina.servico;

import com.cantina.modelo.Produto;
import com.cantina.repositorio.ProdutoRepositorio;
import jakarta.transaction.Transactional;
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

    public List<Produto> listarAtivos() {
        return produtoRepositorio.findByAtivoTrue();
    }

    public Optional<Produto> buscarPorId(Integer id) {
        return produtoRepositorio.findById(id);
    }

    @Transactional
    public Produto salvarProduto(Produto produto) {
        return produtoRepositorio.save(produto);
    }

    @Transactional
    public void toggleAtivo(Integer id) {
        produtoRepositorio.findById(id).ifPresent(p -> {
            p.setAtivo(!p.isAtivo());
            produtoRepositorio.save(p);
        });
    }

    @Transactional
    public void reporStock(Integer id, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        produtoRepositorio.findById(id).ifPresent(p -> {
            p.setQuantidadeEstoque(p.getQuantidadeEstoque() + quantidade);
            produtoRepositorio.save(p);
        });
    }

    public long contarProdutosOk() {
        return produtoRepositorio.findByQuantidadeEstoqueGreaterThan(5).size();
    }

    public long contarStockBaixo() {
        return produtoRepositorio.findByQuantidadeEstoqueBetween(1, 5).size();
    }

    public long contarSemStock() {
        return produtoRepositorio.findByQuantidadeEstoque(0).size();
    }
}
