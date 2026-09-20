package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Prestador.CarregarAreasAtuacaoPrestador;
import com.raoni.chamaja.dto.Prestador.CarregarHomePrestadorDTO;
import com.raoni.chamaja.dto.Prestador.MelhoresDoMesDTO;
import com.raoni.chamaja.dto.Prestador.PrestadorResponseDTO;
import com.raoni.chamaja.service.PrestadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestadores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrestadorController {

    private final PrestadorService prestadorService;

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<List<PrestadorResponseDTO>> buscar(@RequestParam(value = "q", required = false) String termo) {
        List<PrestadorResponseDTO> resultados = prestadorService.buscarPrestadores(termo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity <PrestadorResponseDTO> obterInformacoesDetalhadasPrestador (@PathVariable Long id){
        PrestadorResponseDTO responseDTO = prestadorService.detalharInformacoesPrestador(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/top4")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<List<MelhoresDoMesDTO>> obterTop4MelhoresPrestadores(){
        List<MelhoresDoMesDTO> responseDTOS = prestadorService.top4MelhoresPrestadores();
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping("/carregar-home")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<CarregarHomePrestadorDTO> carregarHomePrestador (){
        CarregarHomePrestadorDTO dto = prestadorService.carregarHomePrestador();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/carregar-categorias")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<CarregarAreasAtuacaoPrestador> carregarCategoriasPrestador (){
        CarregarAreasAtuacaoPrestador dto = prestadorService.carregarAreasAtuacaoPrestador();
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/adicionar-categoria/{id}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Void> adicionarCategoriaPrestador (@PathVariable("id") Long id){
        prestadorService.adicionarCategoria(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/remover-categoria/{id}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Void> removerCategoriaPrestador (@PathVariable("id") Long id){
        prestadorService.removerCategoria(id);
        return ResponseEntity.ok().build();
    }

}
