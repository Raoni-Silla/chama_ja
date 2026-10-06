package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Prestador.*;
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

    @GetMapping("/carregar-carteira")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<CarregarCarteiraPrestadorDTO> carregarCarteiraPrestador (){
        CarregarCarteiraPrestadorDTO dto =  prestadorService.carregarCarteiraPrestador();
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/retirar-saldo/{valor}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Void> retirarSaldoPrestador (@PathVariable("valor")  Double valor){
        prestadorService.retirarValor(valor);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/trocar-chave-pix/{chave}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Void> mudarChavePix(@PathVariable("chave") String chave){
        prestadorService.trocarChavePix(chave);
        return ResponseEntity.ok().build();
    }

}
