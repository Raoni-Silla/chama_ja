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
import { LoginService } from '../../../service/login-service';
import { ButtonModule } from 'primeng/button';
import { InteracaoInicial } from '../../../service/interacao-inicial';
import { PropostaService } from '../../../service/proposta-service';
import { TooltipModule } from 'primeng/tooltip';
import { DialogModule } from 'primeng/dialog';
import { PropostaRequestDTO } from '../../../DTOS/Proposta/PropostaRequestDTO.dto';
import { PropostaResponseDTO } from '../../../DTOS/Proposta/PropostaResponseDTO.dto';

@Component({
  selector: 'app-chat',
  imports: [
    ToastModule,
    CommonModule,
    ProgressSpinnerModule,
    FormsModule,
    ButtonModule,
    TooltipModule,
    DialogModule,
  ],
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
  loginService = inject(LoginService);
  roleUsuario: string | null | undefined = null;
  interacaoInicialService = inject(InteracaoInicial);
  propostaService = inject(PropostaService);
  isModalProposta: boolean = false;
  valorOrcado: number = 0;
  descricao: string = '';
  dataHora: string = '';
  propostaCriada: PropostaResponseDTO | null = null;
  propostaPendente: PropostaResponseDTO | null = null;

  ngOnInit(): void {
    this.carregandoContatos = true;
    this.roleUsuario = this.loginService.obterRoleUsuario();

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
      this.propostaPendente = null;
      this.chatWebsocket.assinarChamado(this.contatoSelecionado.idReferencia, (mensagem) => {
        this.mensagensChamado.push(mensagem);
        this.cdr.detectChanges();
      });

      this.buscarPropostaPendenteParaEsseChamado();

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

  recusarInteracao() {
    if (!this.contatoSelecionado) {
      return;
    }

    const id = this.contatoSelecionado.idReferencia;

    this.interacaoInicialService.recusarInteracao(id).subscribe({
      next: () => {
        this.listaContatos = this.listaContatos.filter(
          (contato) => contato.idReferencia != id || contato.tipoItemCentral != 'INTERACAO',
        );
        if (
          this.contatoSelecionado?.idReferencia === id &&
          this.contatoSelecionado.tipoItemCentral === 'INTERACAO'
        ) {
          this.contatoSelecionado = null;
        }
        this.messageService.add({
          severity: 'success',
          detail: 'Essa interação foi recusada com sucesso',
          life: 3000,
        });
      },
      error: (err) => {
        console.error('mensagem de erro ' + err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos recusar essa interação',
          life: 3000,
        });
      },
    });
  }

  comecarNegociacao() {
    if (!this.contatoSelecionado) {
      return;
    }

    const id = this.contatoSelecionado.idReferencia;

    this.interacaoInicialService.comecarNegociacao(id).subscribe({
      next: (res) => {
        const idChamado = res;
        this.carregandoContatos = true;
        this.chatService.obterContatosUsuario().subscribe({
          next: (res) => {
            this.listaContatos = res;
            const contato = this.listaContatos.find(
              (contato) =>
                contato.idReferencia === idChamado && contato.tipoItemCentral === 'CHAMADO',
            );
            if (!contato) {
              this.carregandoContatos = false;
              return;
            }
            this.carregarInformacoesDaConversa(contato);
            this.carregandoContatos = false;
            this.messageService.add({
              severity: 'success',
              detail: 'Negociação iniciada com sucesso',
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
      },
      error: (err) => {
        console.error('mensagem de erro ' + err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos começar essa interação',
          life: 3000,
        });
      },
    });
  }

  enviarProposta() {
    if (!this.contatoSelecionado?.idReferencia) {
      return;
    }

    const id: number = this.contatoSelecionado?.idReferencia;

    const dto: PropostaRequestDTO = {
      valorOrcado: this.valorOrcado,
      descricao: this.descricao,
      dataHoraServico: this.dataHora,
    };

    this.propostaService.criarProposta(id, dto).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          detail: 'Proposta criada com sucesso',
          life: 3000,
        });
        this.propostaPendente = res;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos lançar essa proposta',
          life: 3000,
        });
      },
    });
  }

  buscarPropostaPendenteParaEsseChamado() {
    if (!this.contatoSelecionado?.idReferencia) {
      return;
    }

    const id: number = this.contatoSelecionado?.idReferencia;
    this.propostaService.obterPropostaPendente(id).subscribe({
      next: (res) => {
        this.propostaPendente = res;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);

        if (err.status === 404) {
          this.messageService.add({
            severity: 'info',
            detail: 'Opa amigo, você ainda não possui propostas pendentes',
            life: 3000,
          });
          this.propostaPendente = null;
        } else {
          this.messageService.add({
            severity: 'error',
            detail: 'Ops, não conseguimos trazer suas propostas pendentes',
            life: 3000,
          });
        }
      },
    });
  }

  recusarProposta() {
    if (!this.propostaPendente?.id) {
      return;
    }

    const id: number = this.propostaPendente?.id;

    this.propostaService.recusarProposta(id).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          detail: 'Proposta recusada com sucesso',
          life: 3000,
        });
        this.propostaPendente = null;
      },
      error: (err) => {
        console.error('error ' + err);
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não deu pra negar essa proposta',
          life: 3000,
        });
      },
    });
  }

  aceitarProposta() {
    if (!this.propostaPendente?.id) {
      return;
    }

    const id: number = this.propostaPendente?.id;

    this.propostaService.aceitarProposta(id).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          detail: 'Proposta aceita com sucesso',
          life: 3000,
        });
        this.propostaPendente = null;
        this.carregandoContatos = true;
        this.chatService.obterContatosUsuario().subscribe({
          next: (res) => {
            this.listaContatos = res;
            this.carregandoContatos = false;
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
      },
      error: (err) => {
        console.error('error ' + err);
        this.carregandoContatos = false;
        this.messageService.add({
          severity: 'error',
          detail: 'Ops, não conseguimos aceitar essa proposta',
          life: 3000,
        });
      },
    });
  }

  get propostaValida(): boolean {
    return this.valorValido() && this.descricaoValida() && this.dataHoraValida();
  }

  private valorValido(): boolean {
    return this.valorOrcado !== null && this.valorOrcado > 0;
  }

  private descricaoValida(): boolean {
    if (!this.descricao?.trim()) {
      return false;
    }

    const palavras = this.descricao.trim().split(/\s+/);

    return palavras.length >= 10;
  }

  private dataHoraValida(): boolean {
    if (!this.dataHora) {
      return false;
    }

    const dataSelecionada = new Date(this.dataHora);

    if (isNaN(dataSelecionada.getTime())) {
      return false;
    }

    const agora = new Date();

    return dataSelecionada > agora;
  }
}
