import { Injectable } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import { MensagemChatDTO } from '../DTOS/Chat/MensagemChatDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class ChatWebsocket {
  private client: Client;
  private subscriptionAtual: StompSubscription | null = null;

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws-chat',
    });

    this.client.onConnect = () => {
      console.log('STOMP conectado com sucesso');
    };
  }

  conectar() {
    const token = sessionStorage.getItem('token_cadastro_chamaja');

    this.client.connectHeaders = {
      Authorization: `Bearer ${token}`,
    };

    this.client.activate();
  }

  desconectar() {
    this.subscriptionAtual?.unsubscribe();
    this.subscriptionAtual = null;
    this.client.deactivate();
  }

  assinarChamado(idChamado: number, aoReceber: (mensagem: MensagemChatDTO) => void) {
    if (this.subscriptionAtual) {
      this.subscriptionAtual.unsubscribe();
    }

    this.subscriptionAtual = this.client.subscribe('/topic/chamado/' + idChamado, (message) => {
      const mensagemRecebida = JSON.parse(message.body) as MensagemChatDTO;

      aoReceber(mensagemRecebida);
    });
  }

  enviarMensagem(idChamado: number, conteudo: string) {
    this.client.publish({
      destination: '/app/chamado/' + idChamado + '/mensagem',
      body: JSON.stringify({
        conteudoMensagem: conteudo,
      }),
    });
  }
}
