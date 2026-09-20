package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Categoria.CategoriaDetalhesDTO;
import com.raoni.chamaja.model.Categoria;
import com.raoni.chamaja.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private CategoriaDetalhesDTO converterParaDTO (Categoria categoria){
        return new CategoriaDetalhesDTO(categoria.getId(),categoria.getNome(), categoria.getIconUrl());
    }
    public List<CategoriaDetalhesDTO> obterOitoCategoriasAleatorias() {
        return categoriaRepository.find8CategoriasAleatorias().stream().map(this::converterParaDTO).toList();
    }

}
