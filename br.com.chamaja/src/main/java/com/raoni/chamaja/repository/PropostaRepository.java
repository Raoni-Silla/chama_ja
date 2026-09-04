package com.raoni.chamaja.repository;

import com.raoni.chamaja.enums.StatusProposta;
import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    boolean existsByChamadoAndStatus (Chamado chamado, StatusProposta status );

    Optional<Proposta> findByChamadoAndStatus(Chamado chamado, StatusProposta status );
}
