package com.cantina.servico;

import com.cantina.modelo.ItemVenda;
import com.cantina.modelo.Produto;
import com.cantina.modelo.Venda;
import com.cantina.repositorio.ProdutoRepositorio;
import com.cantina.repositorio.VendaRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendaServico {

    private final VendaRepositorio vendaRepo;
    private final ProdutoRepositorio produtoRepo;

    public VendaServico(VendaRepositorio vendaRepo, ProdutoRepositorio produtoRepo) {
        this.vendaRepo   = vendaRepo;
        this.produtoRepo = produtoRepo;
    }

    @Transactional
    public Venda iniciarNovaVenda(String nomeCliente, String nomeFuncionario) {
        Venda v = new Venda();
        v.setDataHora(LocalDateTime.now());
        v.setValorTotal(BigDecimal.ZERO);
        v.setNomeCliente(nomeCliente == null || nomeCliente.isBlank() ? "Consumidor Final" : nomeCliente.trim());
        v.setNomeFuncionario(nomeFuncionario != null ? nomeFuncionario : "Sistema");
        v.setEstado(Venda.Estado.ABERTA);
        return vendaRepo.save(v);
    }

    @Transactional
    public Venda adicionarProdutoAVenda(Integer vendaId, Integer produtoId, String nomeProduto, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");

        Venda venda = vendaRepo.findById(vendaId)
            .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        if (!venda.isAberta()) throw new IllegalStateException("Não é possível modificar uma venda " + venda.getEstado().name().toLowerCase() + ".");

        Produto produto;
        if (produtoId != null) {
            produto = produtoRepo.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produtoId));
        } else if (nomeProduto != null && !nomeProduto.isBlank()) {
            produto = produtoRepo.findByNomeContainingIgnoreCase(nomeProduto.trim()).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + nomeProduto));
        } else {
            throw new IllegalArgumentException("Insira um ID ou nome de produto.");
        }

        if (!produto.isAtivo()) throw new IllegalArgumentException("Produto '" + produto.getNome() + "' não está disponível.");

        if (produto.getQuantidadeEstoque() < quantidade)
            throw new IllegalStateException("Stock insuficiente para '" + produto.getNome() +
                "' (Disponível: " + produto.getQuantidadeEstoque() + ")");

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepo.save(produto);

        final Produto pFinal = produto;
        ItemVenda existente = venda.getItens().stream()
            .filter(i -> i.getProduto().getId().equals(pFinal.getId()))
            .findFirst().orElse(null);

        if (existente != null) {
            existente.setQuantidade(existente.getQuantidade() + quantidade);
        } else {
            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());
            venda.getItens().add(item);
        }
        venda.setValorTotal(venda.calcularTotal());
        return vendaRepo.save(venda);
    }

    @Transactional
    public Venda removerProdutoDaVenda(Integer vendaId, Integer produtoId) {
        Venda venda = vendaRepo.findById(vendaId)
            .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));

        if (!venda.isAberta()) throw new IllegalStateException("Não é possível modificar uma venda fechada ou cancelada.");

        ItemVenda item = venda.getItens().stream()
            .filter(i -> i.getProduto().getId().equals(produtoId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Produto não está na venda!"));

        Produto p = item.getProduto();
        p.setQuantidadeEstoque(p.getQuantidadeEstoque() + item.getQuantidade());
        produtoRepo.save(p);
        venda.getItens().remove(item);
        venda.setValorTotal(venda.calcularTotal());
        return vendaRepo.save(venda);
    }

    @Transactional
    public void fecharVenda(Integer vendaId) {
        Venda v = vendaRepo.findById(vendaId)
            .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));
        if (!v.isAberta()) throw new IllegalStateException("Apenas vendas abertas podem ser fechadas.");
        v.setEstado(Venda.Estado.FECHADA);
        v.setValorTotal(v.calcularTotal());
        vendaRepo.save(v);
    }

    @Transactional
    public void cancelarVenda(Integer vendaId) {
        Venda venda = vendaRepo.findById(vendaId)
            .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada!"));
        if (venda.isCancelada()) throw new IllegalStateException("Venda já está cancelada.");
        for (ItemVenda item : venda.getItens()) {
            Produto p = item.getProduto();
            p.setQuantidadeEstoque(p.getQuantidadeEstoque() + item.getQuantidade());
            produtoRepo.save(p);
        }
        venda.setEstado(Venda.Estado.CANCELADA);
        vendaRepo.save(venda);
    }

    public Venda buscarPorId(Integer id) {
        return vendaRepo.findById(id).orElse(null);
    }

    public List<Venda> listarTodas() {
        return vendaRepo.findAll();
    }

    public long contarVendasHoje() {
        return vendaRepo.findVendasFechadasDesde(LocalDate.now().atStartOfDay()).size();
    }

    public BigDecimal receitaHoje() {
        return vendaRepo.findVendasFechadasDesde(LocalDate.now().atStartOfDay())
            .stream()
            .map(Venda::calcularTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
