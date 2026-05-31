package com.cantina.repositorio;

import com.cantina.modelo.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepositorio extends JpaRepository<Venda, Integer> {

    List<Venda> findByEstado(Venda.Estado estado);

    List<Venda> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT v FROM Venda v WHERE v.dataHora >= :inicio AND v.estado = com.cantina.modelo.Venda.Estado.FECHADA")
    List<Venda> findVendasFechadasDesde(@Param("inicio") LocalDateTime inicio);
}
