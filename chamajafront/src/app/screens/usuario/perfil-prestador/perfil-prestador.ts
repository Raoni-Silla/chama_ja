import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { PrestadorResponseDTO } from '../../../DTOS/Prestador/PrestadorResponseDTO.dto';
import { ActivatedRoute, Router } from '@angular/router';
import { Navbarlogged } from '../../../components/navbarlogged/navbarlogged';
import { CommonModule } from '@angular/common';
import { PrestadorService } from '../../../service/prestador-service';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { EditorModule } from 'primeng/editor';
import { UsuarioService } from '../../../service/usuario-service';
import { EnderecoResponseDTO } from '../../../DTOS/Endereco/EnderecoResponseDTO.dto';
import { EnderecoService } from '../../../service/endereco-service';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { InteracaoInicialRequestDTO } from '../../../DTOS/InteracaoInicial/InteracaoInicialRequestDTO.dto';
import { InteracaoInicial } from '../../../service/interacao-inicial';
import { InteracaoInicialResponseDTO } from '../../../DTOS/InteracaoInicial/InteracaoInicialResponseDTO.dto';

@Component({
  selector: 'app-perfil-prestador',
  imports: [
    Navbarlogged,
    CommonModule,
    ProgressSpinnerModule,
    DialogModule,
    ButtonModule,
    ReactiveFormsModule,
    EditorModule,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './perfil-prestador.html',
  styleUrl: './perfil-prestador.css',
})
export class PerfilPrestador implements OnInit {
  prestadorId: number | null = null;
  prestador: PrestadorResponseDTO | null = null;
  carregando: boolean = true;
  isModalAberto: boolean = false;
  titulo = new FormControl('', [Validators.required, Validators.minLength(3)]);
  descricao = new FormControl('', [
    Validators.required,
    Validators.maxLength(100),
    Validators.minLength(10),
  ]);
  valorSugerido = new FormControl(0, [Validators.required, Validators.min(1)]);
  modalConfirmacao: boolean = false;
  enderecosUsuario: EnderecoResponseDTO[] = [];
  carregandoInfoEnderecosModais: boolean = false;
  InteracaoInicialCriada : InteracaoInicialResponseDTO | null = null;

  constructor(
    private route: ActivatedRoute,
    private prestadorService: PrestadorService,
    private cdr: ChangeDetectorRef,
    private usuarioService: UsuarioService,
    private enderecoService: EnderecoService,
    private messageService: MessageService,
    private interacaoInicialService : InteracaoInicial,
    private router : Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.prestadorId = Number(id);
        this.buscarDetalhesDoPrestador(this.prestadorId);
      }
    });
  }

  buscarDetalhesDoPrestador(id: number) {
    this.carregando = true;
    this.prestadorService.buscarPrestadorPorId(id).subscribe({
      next: (dados) => {
        this.prestador = dados;
        console.log(this.prestador);
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao buscar perfil do prestador', erro);
        this.carregando = false;
      },
    });
  }

  carregarModalConfirmacao() {
    this.carregandoInfoEnderecosModais = true;
    this.isModalAberto = false;
    this.modalConfirmacao = true;

    this.usuarioService.obterTodosEnderecosUsuarioLogado().subscribe({
      next: (res) => {
        console.log('RESPOSTA ', res);
        this.carregandoInfoEnderecosModais = false;
        this.enderecosUsuario = res;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  definirEnderecoComoPrincipal(id: number) {
    this.enderecoService.definirNovoEnderecoComoPrincipal(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Endereço definido',
          detail: 'Definimos seu endereço como principal',
          life: 3000,
        });
        this.carregarModalConfirmacao();
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Ops aconteceu algo de errado, tente novamente',
          life: 3000,
        });
      },
    });
  }

  enviarProposta() {
    if (
      this.titulo.valid &&
      this.descricao.valid &&
      this.valorSugerido.valid &&
      this.prestadorId != null
    ) {
      const interacaoInicial: InteracaoInicialRequestDTO = {
        titulo: this.titulo.value!,
        descricao: this.descricao.value!,
        valorSugerido: this.valorSugerido.value!,
        idDestinatario: this.prestadorId,
      };

      this.interacaoInicialService.criarInteracaoInicial(interacaoInicial).subscribe({
        next : (res) => {
          this.InteracaoInicialCriada = res;
           this.messageService.add({
          severity: 'success',
          summary: 'Proposta enviada',
          detail: 'estamos redirecionando você para outra tela',
          life: 3000,
        });
        
        this.router.navigate(['/chat'])

        },
        error : (err) =>{
          console.error(err);
           this.messageService.add({
          severity: 'error',
          summary: 'Ops tivemos um problema, tente novamente',
          life: 3000,
        });
        }
      })
    }
  }
}
