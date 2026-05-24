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

    // Injeção por construtor (boa prática)
    public VendaServico(VendaRepositorio vendaRepositorio, ProdutoRepositorio produtoRepositorio) {
        this.vendaRepositorio = vendaRepositorio;
        this.produtoRepositorio = produtoRepositorio;
    }

    // 1. Cria a venda apenas com o nome do cliente e sem produtos
    @Transactional
    public Venda iniciarNovaVenda(String nomeCliente) {
        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());
        venda.setValorTotal(0.0);
        venda.setNomeCliente(nomeCliente);
        return vendaRepositorio.save(venda);
    }

    // 2. Adiciona um produto à venda aberta e abate o stock
    @Transactional
    public Venda adicionarProdutoAVenda(Integer vendaId, Integer produtoId, int quantidade) {
        Venda venda = vendaRepositorio.findById(vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        Produto produto = produtoRepositorio.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado! ID: " + produtoId));

        // Validar stock
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Stock insuficiente para " + produto.getNome()
                    + " (Disponível: " + produto.getQuantidadeEstoque() + ")");
        }

        // Atualizar stock do produto
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepositorio.save(produto);

        // Verificar se o produto já está no carrinho para apenas somar a quantidade
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

        // Atualizar o valor total da venda
        venda.setValorTotal(venda.calcularTotal());
        return vendaRepositorio.save(venda);
    }

    // 3. Remove um produto do carrinho e devolve o stock
    @Transactional
    public Venda removerProdutoDaVenda(Integer vendaId, Integer produtoId) {
        Venda venda = vendaRepositorio.findById(vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        ItemVenda itemRemover = venda.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Este produto não está na venda!"));

        // Devolver a quantidade ao stock do produto
        Produto produto = itemRemover.getProduto();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + itemRemover.getQuantidade());
        produtoRepositorio.save(produto);

        // Remover da lista da venda
        venda.getItens().remove(itemRemover);

        // Atualizar o valor total da venda
        venda.setValorTotal(venda.calcularTotal());
        return vendaRepositorio.save(venda);
    }

    // 4. Método auxiliar para reexibir a venda em caso de erro
    public Venda buscarPorId(Integer id) {
        return vendaRepositorio.findById(id).orElse(null);
    }

    public List<Venda> listarTodas() {
        return vendaRepositorio.findAll();
    }
}