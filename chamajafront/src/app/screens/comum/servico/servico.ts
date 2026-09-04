import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ProgressSpinner } from 'primeng/progressspinner';
import { ServicoResponseDTO } from '../../../DTOS/Servico/ServicoResponseDTO.dto';
import { ServicosService } from '../../../service/servicos-service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { LoginService } from '../../../service/login-service';

@Component({
  selector: 'app-servico',
  imports: [ProgressSpinner, ToastModule, CurrencyPipe, DatePipe],
  providers: [MessageService],
  templateUrl: './servico.html',
  styleUrl: './servico.css',
})
export class Servico implements OnInit {
  carregando: boolean = false;
  listaServicos: ServicoResponseDTO[] = [];
  private cdr = inject(ChangeDetectorRef);
  private servicoService = inject(ServicosService);
  private messageService = inject(MessageService);
  private loginService = inject(LoginService);
  role : string | null | undefined = '';

  ngOnInit(): void {
    this.carregando = true;
    this.role = this.loginService.obterRoleUsuario();
    this.servicoService.listarServicos().subscribe({
      next: (res) => {
        this.carregando = false;
        this.listaServicos = res;
        this.cdr.detectChanges();
        this.messageService.add({
          severity: 'success',
          summary: 'Servicos carregados com sucesso',
        });
      },
      error: (err) => {
        this.messageService.add({ severity: 'error', summary: 'Algo deu errado, tente novamente' });
      },
    });
  }

  concluirServico(idChamado: number) {
    if (idChamado <= 0 || !idChamado) {
      return;
    }
    this.servicoService.concluirServico(idChamado).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Servico concluido com sucesso',
        });
        this.servicoService.listarServicos().subscribe({
          next: (res) => {
            this.carregando = false;
            this.listaServicos = res;
            this.cdr.detectChanges();
            this.messageService.add({
              severity: 'success',
              summary: 'Servicos carregados com sucesso',
            });
          },
          error: (err) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Algo deu errado, tente novamente',
            });
          },
        });
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Ops, não conseguimos concluir seu serviço',
        });
      },
    });
  }


  

}
