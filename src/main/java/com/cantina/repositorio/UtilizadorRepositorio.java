package com.cantina.repositorio;

import com.cantina.modelo.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilizadorRepositorio extends JpaRepository<Utilizador, Integer> {
    Optional<Utilizador> findByUsername(String username);
    Optional<Utilizador> findByEmail(String email);
    Optional<Utilizador> findByTokenRecuperacao(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
