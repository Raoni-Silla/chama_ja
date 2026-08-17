import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CentralChatService } from '../../../service/central-chat-service';
import { CentralGenericDTO } from '../../../DTOS/Chat/CentralGenericDTO.dto';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { InteracaoInicialResponseDTO } from '../../../DTOS/InteracaoInicial/InteracaoInicialResponseDTO.dto';
import { MensagemChatDTO } from '../../../DTOS/Chat/MensagemChatDTO.dto';
import { ChatWebsocket } from '../../../service/chat-websocket';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat',
  imports: [ToastModule, CommonModule, ProgressSpinnerModule, FormsModule],
  providers: [MessageService],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat implements OnInit, OnDestroy {
  chatService = inject(CentralChatService);
  listaContatos: CentralGenericDTO[] = [];
  cdr = inject(ChangeDetectorRef);
  carregandoContatos: boolean = false;
  carregandoDetalhesContatos: boolean = false;
  messageService = inject(MessageService);
  contatoSelecionado: CentralGenericDTO | null = null;
  detalhesInteracaoInicial: InteracaoInicialResponseDTO | null = null;
  mensagensChamado: MensagemChatDTO[] = [];
  chatWebsocket = inject(ChatWebsocket);
  conteudo: string = '';

  ngOnInit(): void {
    this.carregandoContatos = true;

    this.chatService.obterContatosUsuario().subscribe({
      next: (res) => {
        this.listaContatos = res;
        this.carregandoContatos = false;
        this.messageService.add({
          severity: 'success',
          detail: 'Contatos Carregados com Sucesso',
          life: 3000,
        });
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, tivemos um problema para carregar seus contatos',
          life: 3000,
        });
      },
    });

    this.chatWebsocket.conectar();
  }

  ngOnDestroy(): void {
    this.chatWebsocket.desconectar();
  }

  carregarInformacoesDaConversa(contato: CentralGenericDTO) {
    this.contatoSelecionado = contato;
    this.carregandoDetalhesContatos = true;

    if (this.contatoSelecionado === null) {
      this.messageService.add({
        severity: 'error',
        detail: 'Ops, não conseguimos carregas mais informações desse contato',
        life: 3000,
      });
      return;
    }

    console.log(contato);

    if (this.contatoSelecionado.tipoItemCentral === 'INTERACAO') {
      this.chatService
        .obterDetalhesInteracaoInicial(this.contatoSelecionado.idReferencia)
        .subscribe({
          next: (res) => {
            this.detalhesInteracaoInicial = res;
            this.messageService.add({
              severity: 'success',
              detail: 'Contatos Carregados com Sucesso',
              life: 3000,
            });
            this.carregandoDetalhesContatos = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error(err);
            this.messageService.add({
              severity: 'error',
              detail: 'Ops, não conseguimos carregas mais informações desse contato',
              life: 3000,
            });
            this.carregandoDetalhesContatos = false;
            this.cdr.detectChanges();
          },
        });
    } else if (this.contatoSelecionado.tipoItemCentral === 'CHAMADO') {
      this.chatWebsocket.assinarChamado(this.contatoSelecionado.idReferencia, (mensagem) => {
        this.mensagensChamado.push(mensagem);
        this.cdr.detectChanges();
      });

      this.chatService.obterMensagensChamado(this.contatoSelecionado.idReferencia).subscribe({
        next: (res) => {
          this.mensagensChamado = res;
          this.carregandoDetalhesContatos = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.messageService.add({
            severity: 'error',
            detail: 'Ops, não conseguimos carregar as mensagens desse chamado',
            life: 3000,
          });
          this.carregandoDetalhesContatos = false;
          this.cdr.detectChanges();
        },
      });
    }
  }

  enviarMensagem() {
    if (!this.contatoSelecionado) {
      return;
    }
    const id = this.contatoSelecionado.idReferencia;

    if (this.conteudo === '' || this.conteudo.trim() === '') {
      return;
    }

    this.chatWebsocket.enviarMensagem(id, this.conteudo);

    this.conteudo = '';
  }
}
