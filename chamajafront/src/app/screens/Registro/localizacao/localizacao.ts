import { ChangeDetectorRef, Component, OnInit } from '@angular/core';

import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

import { debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs';

import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { InputTextModule } from 'primeng/inputtext';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';

import { EnderecoService } from '../../../service/endereco-service';
import { GeoapifyFeature } from '../../../DTOS/GeoApi/GeoapifyFeature.dto';
import { EnderecoRequestDTO } from '../../../DTOS/Endereco/EnderecoRequestDTO.dto';
import { LoginService } from '../../../service/login-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-localizacao',
  imports: [
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    ProgressSpinnerModule,
    ReactiveFormsModule,
    ToastModule,
    DialogModule,
    ButtonModule,
  ],
  providers: [MessageService],
  templateUrl: './localizacao.html',
  styleUrl: './localizacao.css',
})
export class Localizacao implements OnInit {
  pegouLocalizacaoAtual = false;

  campoBusca = new FormControl('', {
    nonNullable: true,
  });

  numeroFormControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  complementoFormControl = new FormControl('', {
    nonNullable: true,
  });

  definirComoPrincipal = new FormControl(false, {
    nonNullable: true,
  });

  cepFormControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(8)],
  });

  sugestoes: GeoapifyFeature[] = [];
  enderecoSelecionado: GeoapifyFeature | null = null;

  mostrarModal = false;
  buscandoLocalizacao = false;
  siglaEstado: string = '';

  constructor(
    private enderecoService: EnderecoService,
    private cdr: ChangeDetectorRef,
    private messageService: MessageService,
    private loginService: LoginService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.campoBusca.valueChanges
      .pipe(
        debounceTime(200),

        tap((texto) => {
          if (texto.length <= 2) {
            this.sugestoes = [];
            this.cdr.detectChanges();
          }
        }),

        filter((texto) => texto.length > 2),

        distinctUntilChanged(),

        switchMap((texto) => this.enderecoService.buscarEndereco(texto)),
      )
      .subscribe({
        next: (resultado) => {
          this.sugestoes = resultado.features;
          this.cdr.detectChanges();
        },

        error: (erro) => {
          console.error('Erro ao buscar endereço:', erro);

          this.messageService.add({
            severity: 'error',
            summary: 'Erro na pesquisa',
            detail: 'Não foi possível pesquisar o endereço.',
            life: 3000,
          });
        },
      });
  }

  selecionarEndereco(sugestao: GeoapifyFeature): void {
    this.enderecoSelecionado = sugestao;

    const cepDaApi = sugestao.properties.postcode ?? '';
    this.siglaEstado = sugestao.properties.state_code ?? '';

    this.cepFormControl.setValue(cepDaApi);
    this.sugestoes = [];
    this.mostrarModal = true;
  }

  obterLocalizacaoAtual(): void {
    if (!navigator.geolocation) {
      this.messageService.add({
        severity: 'error',
        summary: 'Geolocalização indisponível',
        detail: 'Seu navegador não suporta geolocalização.',
        life: 3000,
      });

      return;
    }

    this.buscandoLocalizacao = true;

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const latitude = position.coords.latitude;
        const longitude = position.coords.longitude;

        this.buscarEnderecoPorCoordenadas(latitude, longitude);
      },

      (erro) => {
        this.buscandoLocalizacao = false;

        if (erro.code === erro.PERMISSION_DENIED) {
          this.messageService.add({
            severity: 'error',
            summary: 'Permissão negada',
            detail: 'Você negou o acesso à sua localização.',
            life: 3000,
          });

          return;
        }

        if (erro.code === erro.POSITION_UNAVAILABLE || erro.code === erro.TIMEOUT) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Localização indisponível',
            detail: 'Não foi possível encontrar sua localização atual.',
            life: 3000,
          });
        }
      },

      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    );
  }

  private buscarEnderecoPorCoordenadas(latitude: number, longitude: number): void {
    this.enderecoService.buscarEnderecoPorCoordenadas(latitude, longitude).subscribe({
      next: (resultado) => {
        this.pegouLocalizacaoAtual = true;
        this.buscandoLocalizacao = false;

        if (resultado.features?.length > 0) {
          this.selecionarEndereco(resultado.features[0]);
          return;
        }

        this.messageService.add({
          severity: 'warn',
          summary: 'Endereço não encontrado',
          detail: 'Não encontramos a rua exata da sua localização.',
          life: 3000,
        });
      },

      error: (erro) => {
        this.buscandoLocalizacao = false;

        console.error('Erro ao buscar endereço pelas coordenadas:', erro);

        this.messageService.add({
          severity: 'error',
          summary: 'Erro na localização',
          detail: 'Não foi possível obter seu endereço.',
          life: 3000,
        });
      },
    });
  }

  confirmarEEnviarEndereco(): void {
    if (
      !this.enderecoSelecionado ||
      this.numeroFormControl.invalid ||
      this.cepFormControl.invalid
    ) {
      this.numeroFormControl.markAsTouched();
      this.cepFormControl.markAsTouched();
      return;
    }

    const dados = this.enderecoSelecionado.properties;

    const dto: EnderecoRequestDTO = {
      logradouro: dados.street || dados.formatted,
      numero: Number(this.numeroFormControl.value),
      complemento: this.complementoFormControl.value.trim() || undefined,
      nomeCidade: dados.city ?? '',
      siglaEstado: dados.state_code ?? dados.county_code ?? '',
      cep: this.cepFormControl.value,
      latitude: dados.lat,
      longitude: dados.lon,
      enderecoPrincipal: this.definirComoPrincipal.value,
    };

    this.loginService.salvarEnderecoCadastroTemporario(dto).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Endereço adicionado',
          detail: 'Seu endereço foi cadastrado com sucesso.',
          life: 3000,
        });

        this.mostrarModal = false;
        this.limparFormulario();

        this.router.navigate(['register/tipousuario']);
      },

      error: (erro) => {
        console.error('Erro ao salvar endereço:', erro);

        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao salvar endereço',
          detail: 'Não conseguimos salvar seu endereço.',
          life: 3000,
        });
      },
    });
  }

  fecharModal(): void {
    this.mostrarModal = false;
    this.limparFormulario();
  }

  private limparFormulario(): void {
    this.enderecoSelecionado = null;
    this.sugestoes = [];

    this.campoBusca.reset();
    this.numeroFormControl.reset();
    this.complementoFormControl.reset();
    this.cepFormControl.reset();
    this.definirComoPrincipal.reset();
  }
}
