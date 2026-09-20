import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbarlogged } from '../../../components/navbarlogged/navbarlogged';
import { FormsModule } from '@angular/forms';
import { CardBestOfMonth } from '../../../components/card-best-of-month/card-best-of-month';
import { MelhoresDoMesDTO } from '../../../DTOS/MelhoresDoMes/MelhoresDoMesDTO.dto';
import { HighlightAreas } from '../../../components/highlight-areas/highlight-areas';
import { Router } from '@angular/router';
import { UsuarioService } from '../../../service/usuario-service';
import { UsuarioInfoBasicasDTO } from '../../../DTOS/Usuario/UsuarioInfoBasica.dto';
import { CommonModule } from '@angular/common';
import { PrestadorService } from '../../../service/prestador-service';
import { PrestadorResponseDTO } from '../../../DTOS/Prestador/PrestadorResponseDTO.dto';
import { CategoriaService } from '../../../service/categoria-service';
import { CategoriaDetalhesDTO } from '../../../DTOS/Categoria/CategoriaDetalhesDTO.dto';
import { BotaoLogout } from '../../../components/botao-logout/botao-logout';

@Component({
  selector: 'app-home',
  imports: [ FormsModule, CardBestOfMonth, HighlightAreas, CommonModule, BotaoLogout],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  procura: string = ''
  nomeUsuario: string = 'Usuário';
  localizacao: string = '';
  estado: string = '';

  listaMelhoresAreas: CategoriaDetalhesDTO[] = [];

  listaMelhoresMes: MelhoresDoMesDTO[] = [];

  termoBusca: string = '';

  constructor(private router: Router, private usuarioService: UsuarioService, private cdr: ChangeDetectorRef, private prestadorService: PrestadorService, private categoriaService: CategoriaService) { }

  ngOnInit() {
    this.usuarioService.obterInfosBasicasUsuarioLogado().subscribe({
      next: (resposta: UsuarioInfoBasicasDTO) => {
        const primeiroNome = resposta.nome.split(" ")[0];
        this.nomeUsuario = primeiroNome;
        this.localizacao = resposta.endereco;
        this.estado = resposta.estado
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Erro na requisição:', err);
      }
    });

    this.prestadorService.obterTop5MelhoresPrestadores().subscribe({
      next: (resposta: MelhoresDoMesDTO[]) => {
        this.listaMelhoresMes = resposta
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });

    this.categoriaService.obterOitoCategorias().subscribe({
      next: (resposta) => {
        this.listaMelhoresAreas = resposta;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    })

  }


  pesquisar(termoRapido?: string) {
    const termoFinal = termoRapido || this.termoBusca;
    if (termoFinal && termoFinal.trim() !== '') {
      this.router.navigate(['/resultados-busca'], { queryParams: { q: termoFinal } });
    }
  }

  abrirDetalhesPrestador(prestadorId: number) {
    console.log('Prestador selecionado:', prestadorId);
    this.router.navigate(['/prestador', prestadorId]);
  }
}
