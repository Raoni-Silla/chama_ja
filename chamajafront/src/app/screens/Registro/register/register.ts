import { Component } from '@angular/core';
import { Defaultbutton } from '../../../components/defaultbutton/defaultbutton';
import { LoginService } from '../../../service/login-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { InputMaskModule } from 'primeng/inputmask';
import { MessageService } from 'primeng/api';
import { CadastroRequestDTO } from '../../../DTOS/Cadastro/CadastroRequestDTO.dto';
import { InputTextModule } from 'primeng/inputtext';


@Component({
  selector: 'app-register',
  imports: [Defaultbutton, CommonModule, FormsModule, RouterModule, ToastModule, InputMaskModule, InputTextModule],
  providers: [MessageService],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {


  constructor(private loginService: LoginService, private messageService: MessageService, private router: Router) {}

  nome: string = '';
  cpf: string = '';
  dataNascimento: string = '';
  email: string = '';
  senha: string = '';
  emailRegex: RegExp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?)*$/;
  senhaRegex: RegExp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#_-])[A-Za-z\d@$!%*?&#_-]{8,}$/;

  iniciarCadastro(){
    if (!this.emailRegex.test(this.email)) {
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Ops!', 
        detail: 'Esse formato de email é inválido.', 
        life: 3000 
      });
      return; 
    }

    const cpfLimpo = this.cpf.replace(/\D/g, '');
    if (cpfLimpo.length !== 11) {
      this.messageService.add({ 
        severity: 'error', 
        summary: 'CPF Inválido', 
        detail: 'O CPF precisa ter exatamente 11 números.', 
        life: 3000 
      });
      return;
    }

    if (this.senha.length < 6) {
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Senha Curta', 
        detail: 'Sua senha deve ter no mínimo 6 caracteres.', 
        life: 3000 
      });
      return;
    }

    if (!this.senhaRegex.test(this.senha)) {
      this.messageService.add({ 
        severity: 'warn', 
        summary: 'Senha Fraca', 
        detail: 'A senha precisa ter no mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas, números e um caractere especial.', 
        life: 5000 
      });
      return;
    }

    const parseData = this.dataNascimento.split('/');
    const dataFormatada = `${parseData[2]}-${parseData[1]}-${parseData[0]}`;
  

    const dto : CadastroRequestDTO = {
      nome: this.nome,
      cpf: cpfLimpo,
      dataNascimento: dataFormatada,
      email: this.email,
      senha: this.senha
    };

    console.log('DTO enviado para o serviço:', dto);

   this.loginService.iniciarCadastro(dto).subscribe({
      next: (response) => {
        this.loginService.salvarToken(response.token);    
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso!',
          detail: 'Cadastro iniciado com sucesso. Continuando seu cadastro...',
          life: 3000
        });
        
      this.router.navigate(['/register/telefone']);
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Não foi possivel continuar seu cadastro',
          detail: 'Cadastro não iniciado com sucesso.',
          life: 3000
        });
      }
    });
   
  }
  
}
