package com.cantina.repositorio;

import com.cantina.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepositorio extends JpaRepository<Produto, Integer> {

    List<Produto> findByQuantidadeEstoqueGreaterThan(int quantidade);

    List<Produto> findByQuantidadeEstoqueBetween(int min, int max);

    List<Produto> findByQuantidadeEstoque(int quantidade);
}