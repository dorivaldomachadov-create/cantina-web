package com.cantina.servico;

import com.cantina.dto.ItemVendaRequest;
import com.cantina.dto.VendaRequest;
import com.cantina.modelo.ItemVenda;
import com.cantina.modelo.Produto;
import com.cantina.modelo.Venda;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.repositorio.VendaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Transactional
    public Venda realizarVenda(VendaRequest request) {
        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());

        List<ItemVenda> itensVenda = new ArrayList<>();
        double valorTotalVenda = 0.0;

        for (ItemVendaRequest itemDto : request.getItens()) {
            // 1. Procurar o produto na base de dados
            Produto produto = produtoRepositorio.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado! ID: " + itemDto.getProdutoId()));

            // 2. Validar stock disponível
            if (produto.getQuantidadeEstoque() < itemDto.getQuantidade()) {
                throw new RuntimeException("Stock insuficiente para o produto: " + produto.getNome()
                        + " (Disponível: " + produto.getQuantidadeEstoque() + ")");
            }

            // 3. Atualizar o stock do produto
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDto.getQuantidade());
            produtoRepositorio.save(produto); // Guarda a atualização do stock

            // 4. Criar a linha do item da venda
            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco()); // Fixa o preço atual
            item.setVenda(venda);

            itensVenda.add(item);

            // 5. Acumular o valor total
            valorTotalVenda += item.getPrecoUnitario() * item.getQuantidade();
        }

        venda.setItens(itensVenda);
        venda.setValorTotal(valorTotalVenda);

        // 6. Salvar a venda completa (grava a venda e os itens em cascata)
        return vendaRepositorio.save(venda);
    }
}