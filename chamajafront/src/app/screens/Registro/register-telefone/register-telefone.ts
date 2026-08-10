import { Component, OnInit, signal } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { InputMaskModule } from 'primeng/inputmask';
import { MessageService } from 'primeng/api';
import { LoginService } from '../../../service/login-service';
import { InputOtpModule } from 'primeng/inputotp';
import { ButtonModule } from 'primeng/button';
import { Defaultbutton } from '../../../components/defaultbutton/defaultbutton';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-register-telefone',
  imports: [Defaultbutton, CommonModule, FormsModule, RouterModule, ToastModule, InputMaskModule, InputOtpModule, ButtonModule , InputTextModule],
  providers: [MessageService],
  templateUrl: './register-telefone.html',
  styleUrl: './register-telefone.css',
})
export class RegisterTelefone implements OnInit {

  telefone: string = '';

  etapaVerificacao: boolean = false;
  codigoSms: string = '';
  segundosRestantes = signal(200);
  bloqueado = false;
  tempoRestante = 0;

  constructor(private messageService: MessageService, private loginService: LoginService, private router: Router) { }

  ngOnInit() {
    const token = this.loginService.obterToken();
    if (!token) {
      this.router.navigate(['/register']);
    }
  }

  async handleClick() {
    if (this.bloqueado) {
      return;
    }

    this.bloqueado = true;
    this.tempoRestante = 120;

    try {
      await this.fazerRequisicao();
    } catch (error) {
      console.error(error);
    }

    const interval = setInterval(() => {
      this.tempoRestante--;

      if (this.tempoRestante <= 0) {
        clearInterval(interval);
        this.bloqueado = false;
      }
    }, 1000);
  }

  async fazerRequisicao() {
    this.loginService.solicitarEnvioSMS().subscribe({
      next: (response) => {
        this.messageService.add({
          severity: "success",
          summary: 'Codigo enviado com sucesso',
          detail: 'acabamos de enviar o codigo pro seu telefone',
          life: 3000
        })
      }
    })
  }

  enviarTelefone() {
    const telefoneLimpo = this.telefone.trim().replace(/\D/g, '');
    if (telefoneLimpo.length !== 11) {
      this.messageService.add({
        severity: 'error',
        summary: 'Telefone Inválido',
        detail: 'O telefone precisa ter exatamente 11 números.',
        life: 3000
      });
      return;
    }

    this.loginService.adicionarTelefone(telefoneLimpo).subscribe({
      next: (response) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Telefone Adicionado',
          detail: 'Enviamos um SMS de confirmação para o seu telefone.',
        });
        this.etapaVerificacao = true;
        this.loginService.solicitarEnvioSMS().subscribe({
          next: (smsResponse) => {
            this.messageService.add({
              severity: 'success',
              summary: 'SMS Enviado',
              detail: 'O código de verificação foi enviado para o seu telefone.',
              life: 3000
            });

          }
        });
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao adicionar telefone',
          detail: 'Ocorreu um erro ao adicionar o telefone. Por favor, tente novamente.',
          life: 3000
        });
        this.etapaVerificacao = false;
      }
    });
  }

  verificarCodigo() {
    if (this.codigoSms.trim().length !== 6 || !/^\d+$/.test(this.codigoSms.trim()) || this.codigoSms.trim() === '') {
      this.messageService.add({
        severity: 'error',
        summary: 'Código Inválido',
        detail: 'O código de verificação precisa ter exatamente 6 dígitos.',
        life: 3000
      });
      return;
    }

    // Chamada limpa, enviando apenas o código
    this.loginService.confirmarCodigoSMS(this.codigoSms.trim()).subscribe({
      next: (response) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Telefone Verificado',
          detail: 'Seu telefone foi verificado com sucesso.',
          life: 3000
        });
        this.router.navigate(['register/localizacao']);
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro na verificação',
          detail: 'Ocorreu um erro ao verificar o código. Por favor, tente novamente.',
          life: 3000
        });
      }
    });
  }

  voltar() {
    this.etapaVerificacao = false;
    this.codigoSms = '';
    this.telefone = '';
  }
}