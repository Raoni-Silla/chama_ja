package com.raoni.chamaja.dto.Prestador;

import com.raoni.chamaja.dto.Categoria.CategoriaDetalhesDTO;

import java.util.List;

public record CarregarAreasAtuacaoPrestador(
        List<CategoriaDetalhesDTO> listaCategoriasPrestador,
        List<CategoriaDetalhesDTO> categoriasDisponiveis
) {
}
