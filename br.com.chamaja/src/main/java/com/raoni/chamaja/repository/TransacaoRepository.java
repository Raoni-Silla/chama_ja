package com.raoni.chamaja.repository;

import com.raoni.chamaja.model.TransacaoCarteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoCarteira, Long> {
}
