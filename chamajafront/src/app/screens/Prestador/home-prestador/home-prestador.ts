import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePickerModule } from 'primeng/datepicker';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { PrestadorService } from '../../../service/prestador-service';
import { InteracaoInicial } from '../../../service/interacao-inicial';
import { CarregarHomePrestadorDTO } from '../../../DTOS/Prestador/CarregarHomePrestadorDTO.dto';
import { InteracaoIniciaInfoUteisParaPrestador } from '../../../DTOS/InteracaoInicial/InteracaoInicialInfoUteisParaPrestador.dto';

@Component({
  selector: 'app-home-prestador',
  imports: [FormsModule, DatePickerModule, CommonModule, ToastModule, ProgressSpinnerModule],
  providers: [MessageService],
  templateUrl: './home-prestador.html',
  styleUrl: './home-prestador.css',
})
export class HomePrestador implements OnInit {
  dataSelecionada: Date = new Date(2026, 1, 23);

  prestadorService = inject(PrestadorService);
  interacaoInicialService = inject(InteracaoInicial);
  router = inject(Router);
  messageService = inject(MessageService);
  cdr = inject(ChangeDetectorRef);

  carregando: boolean = true;
  dadosHome: CarregarHomePrestadorDTO | null = null;

  processandoInteracao: number | null = null;

  ngOnInit(): void {
    this.dataSelecionada = new Date();
    this.carregarDados();
  }

  carregarDados(): void {
    this.carregando = true;

    this.prestadorService.carregarHomePrestador().subscribe({
      next: (res) => {
        this.dadosHome = res;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos carregar sua home',
          life: 3000,
        });
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  verDetalhesProximoServico(): void {
    this.router.navigate(['/servicos']);
  }

  aceitarSolicitacao(solicitacao: InteracaoIniciaInfoUteisParaPrestador): void {
    if (this.processandoInteracao !== null) {
      return;
    }

    this.processandoInteracao = solicitacao.idInteracao;

    this.interacaoInicialService.comecarNegociacao(solicitacao.idInteracao).subscribe({
      next: (idChamado) => {
        this.processandoInteracao = null;
        this.router.navigate(['/chat'], { queryParams: { idChamado } });
      },
      error: (err) => {
        console.error(err);
        this.processandoInteracao = null;
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos iniciar essa negociação',
          life: 3000,
        });
        this.cdr.detectChanges();
      },
    });
  }

  recusarSolicitacao(solicitacao: InteracaoIniciaInfoUteisParaPrestador): void {
    if (this.processandoInteracao !== null) {
      return;
    }

    this.processandoInteracao = solicitacao.idInteracao;

    this.interacaoInicialService.recusarInteracao(solicitacao.idInteracao).subscribe({
      next: () => {
        this.processandoInteracao = null;

        if (this.dadosHome) {
          this.dadosHome.solicitacoesPendentes = this.dadosHome.solicitacoesPendentes.filter(
            (s) => s.idInteracao !== solicitacao.idInteracao,
          );
        }

        this.messageService.add({
          severity: 'success',
          detail: 'Solicitação recusada com sucesso',
          life: 3000,
        });

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.processandoInteracao = null;
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos recusar essa solicitação',
          life: 3000,
        });
        this.cdr.detectChanges();
      },
    });
  }
}