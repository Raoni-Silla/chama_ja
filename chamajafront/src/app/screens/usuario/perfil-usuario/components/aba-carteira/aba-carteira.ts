import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { DialogModule } from 'primeng/dialog';
import { PrestadorService } from '../../../../../service/prestador-service';
import { CarregarCarteiraPrestadorDTO } from '../../../../../DTOS/Prestador/CarregarCarteiraPrestadorDTO.dto';

@Component({
  selector: 'app-aba-carteira',
  imports: [CommonModule, FormsModule, ToastModule, ProgressSpinnerModule, DialogModule],
  providers: [MessageService],
  templateUrl: './aba-carteira.html',
  styleUrl: './aba-carteira.css',
})
export class AbaCarteira implements OnInit {
  prestadorService = inject(PrestadorService);
  messageService = inject(MessageService);
  cdr = inject(ChangeDetectorRef);

  carregando: boolean = true;
  dadosCarteira: CarregarCarteiraPrestadorDTO | null = null;

  modalRetirarSaldoIsOpen: boolean = false;
  valor: number = 0;

  modalTrocarChavePix: boolean = false;
  novaChavePix: string = '';

  // Sem id nas listas do DTO, então os avatares recebem cor por posição,
  // ciclando entre as 5 cores já definidas no CSS (corA..corE).
  private readonly coresAvatar = ['corA', 'corB', 'corC', 'corD', 'corE'];

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.carregando = true;

    this.prestadorService.carregarCarteira().subscribe({
      next: (res) => {
        this.dadosCarteira = res;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos carregar sua carteira',
          life: 3000,
        });
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  abrirModalSaque(): void {
    this.valor = 0;
    this.modalRetirarSaldoIsOpen = true;
  }

  retirarSaldo() {
    if (this.valor <= 0) {
      this.messageService.add({
        severity: 'error',
        detail: 'Ops, impossivel sacar esse valor',
        life: 3000,
      });
      return;
    }

    this.prestadorService.retirarSaldo(this.valor).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          detail: 'Saldo Retirado com sucesso',
          life: 3000,
        });
        this.modalRetirarSaldoIsOpen = false;
        this.valor = 0;
        this.cdr.detectChanges();
        this.carregarDados();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos carregar sua carteira',
          life: 3000,
        });
        console.error(err);
        this.cdr.detectChanges();
      },
    });
  }

  abrirModalTrocarChave(): void {
    this.novaChavePix = this.dadosCarteira?.chavePix ?? '';
    this.modalTrocarChavePix = true;
  }

  trocarChavePix() {
    if (!this.novaChavePix || !this.novaChavePix.trim()) {
      this.messageService.add({
        severity: 'error',
        detail: 'Ops, digite uma chave PIX válida',
        life: 3000,
      });
      return;
    }

    this.prestadorService.trocarChavePix(this.novaChavePix.trim()).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          detail: 'Chave PIX atualizada com sucesso',
          life: 3000,
        });
        this.modalTrocarChavePix = false;
        this.novaChavePix = '';
        this.cdr.detectChanges();
        this.carregarDados();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos atualizar sua chave PIX',
          life: 3000,
        });
        console.error(err);
        this.cdr.detectChanges();
      },
    });
  }

  obterCorAvatar(index: number): string {
    return this.coresAvatar[index % this.coresAvatar.length];
  }

  obterIconeTransacao(tipo: string): string {
    switch (tipo) {
      case 'ENTRADA_SERVICO':
        return 'pi-wallet';
      case 'SAQUE_PIX':
        return 'pi-arrow-up';
      case 'ESTORNO':
        return 'pi-replay';
      case 'TAXA_PLATAFORMA':
        return 'pi-percentage';
      default:
        return 'pi-circle';
    }
  }

  obterLabelTransacao(tipo: string): string {
    switch (tipo) {
      case 'ENTRADA_SERVICO':
        return 'Pagamento recebido';
      case 'SAQUE_PIX':
        return 'Saque via PIX';
      case 'ESTORNO':
        return 'Estorno';
      case 'TAXA_PLATAFORMA':
        return 'Taxa da plataforma';
      default:
        return tipo;
    }
  }

  ehTransacaoPositiva(tipo: string): boolean {
    return tipo === 'ENTRADA_SERVICO';
  }

  copiarChave(): void {
    if (!this.dadosCarteira?.chavePix) {
      return;
    }

    navigator.clipboard.writeText(this.dadosCarteira.chavePix).then(() => {
      this.messageService.add({
        severity: 'success',
        detail: 'Chave PIX copiada',
        life: 2000,
      });
    });
  }
}