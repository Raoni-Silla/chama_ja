package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Categoria.CategoriaDetalhesDTO;
import com.raoni.chamaja.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping(path = "/obter-8-categorias")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<List<CategoriaDetalhesDTO>> obterOitoCategorias (){
        List<CategoriaDetalhesDTO> responseDTOS = categoriaService.obterOitoCategoriasAleatorias();
        return ResponseEntity.ok(responseDTOS);
    }

}
