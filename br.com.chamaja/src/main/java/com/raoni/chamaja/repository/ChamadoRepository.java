package com.raoni.chamaja.repository;

import com.raoni.chamaja.enums.StatusChamado;
import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.Prestador;
import com.raoni.chamaja.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    boolean existsByClienteAndPrestadorAndStatusChamado(Usuario usuario, Prestador prestador, StatusChamado status);
    List<Chamado> findByClienteAndStatusChamadoIn(Usuario cliente, List<StatusChamado> statusList);
    List<Chamado> findByPrestadorAndStatusChamadoIn(Prestador prestador, List<StatusChamado> statusList);

}
