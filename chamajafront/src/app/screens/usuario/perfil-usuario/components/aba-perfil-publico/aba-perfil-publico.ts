import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UsuarioService } from '../../../../../service/usuario-service';
import { MessageService } from 'primeng/api';
import { UsuarioInfoPerfilDTO } from '../../../../../DTOS/Usuario/UsuarioInfoPerfilDTO.dto';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
@Component({
  selector: 'app-aba-perfil-publico',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    ProgressSpinnerModule,
  ],
  providers: [MessageService],
  templateUrl: './aba-perfil-publico.html',
  styleUrl: './aba-perfil-publico.css',
})
export class AbaPerfilPublico implements OnInit {
  urlFoto: string = '';
  perfilForm!: FormGroup;
  verificado: boolean = false;
  cpf: string = '';
  telefoneOriginal = '';
  modalVisivel = false;
  codigoSms = '';
  carregando = false;

  constructor(
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private usuarioService: UsuarioService,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.carregando = true;

    this.perfilForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      raioBusca: [null, [Validators.required, Validators.min(1), Validators.max(100)]],
    });

    this.usuarioService.obterInfosParaTelaDePerfil().subscribe({
      next: (resposta) => {
        this.verificado = resposta.verificado;
        this.urlFoto = resposta.urlFoto;
        this.cpf = resposta.cpf;

        this.telefoneOriginal = resposta.telefone;

        this.perfilForm.patchValue({
          nome: resposta.nome,
          email: resposta.email,
          telefone: resposta.telefone,
          raioBusca: resposta.raioBusca / 1000
        });

        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.carregando = false;
        console.error(err);
      },
    });
  }

  enviarCodigoSms(telefone: string) {
    this.usuarioService.enviarCodigoSms(telefone).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Codigo sms enviado com sucesso',
          detail: 'Por favor confira sua caixa de entrada',
          life: 3000,
        });
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao enviar sms',
          detail: 'Não conseguimos enviar o sms, tente novamente',
          life: 3000,
        });
      },
    });
  }

  executarSalvamentoFinal() {
    if (this.perfilForm.valid) {
      const dto: UsuarioInfoPerfilDTO = {
        nome: this.perfilForm.get('nome')?.value,
        email: this.perfilForm.get('email')?.value,
        telefone: this.perfilForm.get('telefone')?.value,
        urlFoto: this.urlFoto,
        cpf: this.cpf,
        verificado: this.verificado,
        raioBusca:this.perfilForm.get('raioBusca')?.value
      };

      this.usuarioService.salvarInfosModificadasDaTelaDePerfil(dto).subscribe({
        next: (resposta) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Dados alterados com sucesso',
            detail: 'Suas Informações foram salvas com sucesso',
            life: 3000,
          });

          this.urlFoto = resposta.urlFoto;
          this.cpf = resposta.cpf;

          this.perfilForm.patchValue({
            nome: resposta.nome,
            email: resposta.email,
            telefone: resposta.telefone,
          });

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.messageService.add({
            severity: 'error',
            summary: 'Encontramos um erro',
            detail: 'Impossivel fazer alterações no momento, tente denovo mais tarde',
            life: 3000,
          });
        },
      });

      this.perfilForm.markAsPristine();
    }
  }

  salvarAlteracoes() {
    if (this.perfilForm.valid) {
      if (this.telefoneOriginal != this.perfilForm.get('telefone')?.value) {
        this.showDialog();
        this.enviarCodigoSms(this.perfilForm.get('telefone')?.value);
        return;
      } else {
        this.executarSalvamentoFinal();
      }
    }
  }

  validarSms() {
    if (this.codigoSms === '' || this.codigoSms.length < 6) {
      return;
    }

    this.usuarioService.validarCodigoSms(this.codigoSms).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Numero validado com sucesso',
          detail: 'Numero validado e alterado com sucesso',
          life: 3000,
        });
        this.modalVisivel = false;
        this.executarSalvamentoFinal();
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Código Inválido',
          detail: 'O código digitado está incorreto ou expirou. Tente novamente.',
          life: 3000,
        });
      },
    });
  }

  showDialog() {
    this.modalVisivel = true;
  }
}
