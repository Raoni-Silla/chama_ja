package com.raoni.chamaja.repository;

import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByChamado(Chamado chamado);
}
