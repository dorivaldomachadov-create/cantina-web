package com.cantina.repositorio;

import com.cantina.modelo.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepositorio extends JpaRepository<Venda, Integer> {
    // O Spring Data JPA vai gerar automaticamente todos os métodos como o .save()
}