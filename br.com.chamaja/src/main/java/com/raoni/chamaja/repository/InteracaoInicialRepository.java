package com.raoni.chamaja.repository;

import com.raoni.chamaja.enums.StatusInteracao;
import com.raoni.chamaja.model.InteracaoInicial;
import com.raoni.chamaja.model.Prestador;
import com.raoni.chamaja.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteracaoInicialRepository extends JpaRepository<InteracaoInicial,Long> {
    boolean existsByRemetenteAndDestinatarioAndStatus(Usuario remetente, Prestador destinatario, StatusInteracao status);
    List<InteracaoInicial> findByDestinatarioAndStatus(Prestador prestador, StatusInteracao statusInteracao);
    List<InteracaoInicial> findByRemetenteAndStatus(Usuario remetente, StatusInteracao status);
}
