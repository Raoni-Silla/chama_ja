import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { Navbarlogged } from '../../../components/navbarlogged/navbarlogged';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../../service/usuario-service';
import { UsuarioInfoPerfilDTO } from '../../../DTOS/Usuario/UsuarioInfoPerfilDTO.dto';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { FileUploadModule } from 'primeng/fileupload';
import { ButtonModule } from 'primeng/button';
import { AbaPerfilPublico } from './components/aba-perfil-publico/aba-perfil-publico';
import { AbaConta } from './components/aba-conta/aba-conta';
import { AbaEnderecos } from './components/aba-enderecos/aba-enderecos';
import { AbaCartoes } from './components/aba-cartoes/aba-cartoes';
import { LoginService } from '../../../service/login-service';
import { AbaAreas } from './components/aba-areas/aba-areas';
import { AbaCarteira } from './components/aba-carteira/aba-carteira';

interface UploadEvent {
  originalEvent: Event;
  files: File[];
}

type AbaPerfil = 'perfil' | 'conta' | 'enderecos' | 'cartoes' | 'areas' | 'carteira';

@Component({
  selector: 'app-perfil-usuario',
  imports: [Navbarlogged, ToastModule, AbaPerfilPublico, AbaConta, AbaEnderecos, AbaCartoes, AbaAreas, AbaCarteira],
  providers: [MessageService],
  templateUrl: './perfil-usuario.html',
  styleUrl: './perfil-usuario.css',
})
export class PerfilUsuario {
  private loginService = inject(LoginService);

  roleUsuario = this.loginService.obterRoleUsuario() ?? '';

  isPrestador = this.roleUsuario === 'PRESTADOR';

  abaAtiva: AbaPerfil = 'perfil';

  mudarAba(aba: AbaPerfil): void {
    if (!this.isPrestador && (aba === 'areas' || aba === 'carteira')) {
      return;
    }

    this.abaAtiva = aba;
  }
}
