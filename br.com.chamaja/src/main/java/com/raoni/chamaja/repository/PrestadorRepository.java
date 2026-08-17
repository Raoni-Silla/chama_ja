package com.raoni.chamaja.repository;

import com.raoni.chamaja.enums.StatusInteracao;
import com.raoni.chamaja.model.InteracaoInicial;
import com.raoni.chamaja.model.Prestador;
import com.raoni.chamaja.projection.PrestadorProximoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
    @Query(value = """
            SELECT DISTINCT
                p.id AS id,
            
                ST_Distance(
                    ep.ponto_geografico,
                    eu.ponto_geografico
                ) / 1000.0 AS "distanciaKm"
            
            FROM prestador p
             
            JOIN usuarios u
                ON p.id = u.id
            
            JOIN endereco ep
                ON ep.usuario_id = u.id
                AND ep.endereco_principal = true
            
            JOIN endereco eu
                ON eu.usuario_id = :usuarioId
                AND eu.endereco_principal = true
            
            LEFT JOIN prestador_categoria pc
                ON pc.prestador_id = p.id
            
            LEFT JOIN categoria c
                ON c.id = pc.categoria_id
            
            WHERE
                to_tsvector(
                    'portuguese',
                    coalesce(u.nome, '') || ' ' ||
                    coalesce(ep.nome_cidade, '') || ' ' ||
                    coalesce(c.nome, '')
                )
                @@ websearch_to_tsquery(
                    'portuguese',
                    :termo
                )
            
                AND ep.ponto_geografico IS NOT NULL
                AND eu.ponto_geografico IS NOT NULL
            
                AND ST_DWithin(
                    ep.ponto_geografico,
                    eu.ponto_geografico,
                    :raioMetros
                )
            
            ORDER BY "distanciaKm" ASC
            """,
            nativeQuery = true)
    List<PrestadorProximoProjection> findIdsByBuscaInteligente(
            @Param("termo") String termo,
            @Param("usuarioId") Long usuarioId,
            @Param("raioMetros") double raioMetros
    );


    @Query(value = """
        SELECT
            p.id AS id,

            ST_Distance(
                ep.ponto_geografico,
                eu.ponto_geografico
            ) / 1000.0 AS "distanciaKm"

        FROM prestador p

        JOIN usuarios u
            ON p.id = u.id

        JOIN endereco ep
            ON ep.usuario_id = p.id
            AND ep.endereco_principal = true

        JOIN endereco eu
            ON eu.usuario_id = :usuarioId
            AND eu.endereco_principal = true

        WHERE ep.ponto_geografico IS NOT NULL
          AND eu.ponto_geografico IS NOT NULL

        ORDER BY u.nota_media DESC

        LIMIT 5
        """,
            nativeQuery = true)
    List<PrestadorProximoProjection> findTop5Melhores(
            @Param("usuarioId") Long usuarioId
    );

}
