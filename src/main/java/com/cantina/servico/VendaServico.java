package com.cantina.servico;

import com.cantina.modelo.ItemVenda;
import com.cantina.modelo.Produto;
import com.cantina.modelo.Venda;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.repositorio.VendaRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendaServico {

    private final VendaRepositorio vendaRepositorio;
    private final ProdutoRepositorio produtoRepositorio;

    public VendaServico(VendaRepositorio vendaRepositorio, ProdutoRepositorio produtoRepositorio) {
        this.vendaRepositorio = vendaRepositorio;
        this.produtoRepositorio = produtoRepositorio;
    }

    @Transactional
    public Venda iniciarNovaVenda(String nomeCliente) {
        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());
        venda.setValorTotal(0.0);
        venda.setNomeCliente(nomeCliente);
        venda.setEstado("ABERTA"); // Garante que começa como aberta
        return vendaRepositorio.save(venda);
    }

    @Transactional
    public Venda adicionarProdutoAVenda(Integer vendaId, Integer produtoId, int quantidade) {
        Venda venda = vendaRepositorio.findById(vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        Produto produto = produtoRepositorio.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado! ID: " + produtoId));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Stock insuficiente para " + produto.getNome()
                    + " (Disponível: " + produto.getQuantidadeEstoque() + ")");
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepositorio.save(produto);

        ItemVenda itemExistente = venda.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
        } else {
            ItemVenda novoItem = new ItemVenda();
            novoItem.setVenda(venda);
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantidade);
            novoItem.setPrecoUnitario(produto.getPreco());
            venda.getItens().add(novoItem);
        }

        venda.setValorTotal(venda.calcularTotal());
        return vendaRepositorio.save(venda);
    }

    @Transactional
    public Venda removerProdutoDaVenda(Integer vendaId, Integer produtoId) {
        Venda venda = vendaRepositorio.findById(vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        ItemVenda itemRemover = venda.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Este produto não está na venda!"));

        Produto produto = itemRemover.getProduto();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + itemRemover.getQuantidade());
        produtoRepositorio.save(produto);

        venda.getItens().remove(itemRemover);
        venda.setValorTotal(venda.calcularTotal());
        return vendaRepositorio.save(venda);
    }

    //  Método para repor o estoque e cancelar a venda com segurança
    @Transactional
    public void cancelarVenda(Integer vendaId) {
        Venda venda = vendaRepositorio.findById(vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        // 1. Devolve a quantidade de todos os itens do carrinho de volta para o estoque
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
            produtoRepositorio.save(produto); // Atualiza o estoque do produto
        }

        // 2. Atualiza o estado para CANCELADA
        venda.setEstado("CANCELADA");
        vendaRepositorio.save(venda);
    }

    public Venda buscarPorId(Integer id) {
        return vendaRepositorio.findById(id).orElse(null);
    }

    public List<Venda> listarTodas() {
        return vendaRepositorio.findAll();
    }
}