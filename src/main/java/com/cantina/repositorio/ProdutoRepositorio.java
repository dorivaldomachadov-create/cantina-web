package com.cantina.repositorio;

import com.cantina.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepositorio extends JpaRepository<Produto, Integer> {

}