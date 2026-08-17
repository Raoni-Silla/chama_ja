package com.raoni.chamaja.repository;

import com.raoni.chamaja.model.Endereco;
import com.raoni.chamaja.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {


    List<Endereco> findByUsuarioId(Long idUsuario);

    Optional<Endereco> findByUsuarioIdAndEnderecoPrincipalTrue(Long usuarioId);

    boolean existsByUsuarioIdAndCep(Long usuarioId, String cep);

    @Query(value = """
            SELECT
                ST_Distance(
                    ep.ponto_geografico,
                    eu.ponto_geografico
                ) / 1000.0
            FROM endereco ep
            
            JOIN endereco eu
                ON eu.usuario_id = :usuarioId
                AND eu.endereco_principal = true
            
            WHERE ep.usuario_id = :prestadorId
              AND ep.endereco_principal = true
              AND ep.ponto_geografico IS NOT NULL
              AND eu.ponto_geografico IS NOT NULL
            """,
            nativeQuery = true)
    Optional<Double> calcularDistanciaKm(
            @Param("prestadorId") Long prestadorId,
            @Param("usuarioId") Long usuarioId
    );

}
