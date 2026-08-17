import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { PrestadorResponseDTO } from '../../../DTOS/Prestador/PrestadorResponseDTO.dto';
import { ActivatedRoute, Router } from '@angular/router';
import { PrestadorService } from '../../../service/prestador-service';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CommonModule } from '@angular/common';
import { Navbarlogged } from '../../../components/navbarlogged/navbarlogged';

@Component({
  selector: 'app-resultados-procura-prestadores',
  imports: [ProgressSpinnerModule, CommonModule, Navbarlogged],
  templateUrl: './resultados-procura-prestadores.html',
  styleUrl: './resultados-procura-prestadores.css',
})
export class ResultadosProcuraPrestadores implements OnInit {

  termoPesquisado: string = '';
  prestadoresEncontrados: PrestadorResponseDTO[] = [];
  carregando: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router:Router,
    private prestadorService: PrestadorService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.termoPesquisado = params['q'] || '';
      
      if (this.termoPesquisado) {
        this.buscarNoBackend(this.termoPesquisado);
      }
    });
  }

  buscarNoBackend(termo: string) {

    this.carregando = true;
    
    this.prestadorService.buscarPrestadores(termo).subscribe({
      next: (dados) => {
        console.log('RESPOSTA CHEGOU NO COMPONENTE:', dados);
        this.prestadoresEncontrados = dados;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro na busca da API:', erro);
        this.carregando = false;
      }
    });
  }

  verPerfil(prestadorId: number) {
    this.router.navigate(['/prestador', prestadorId]);
  }
}
