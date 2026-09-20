import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { PrestadorService } from '../../../../../service/prestador-service';
import { CarregarAreasAtuacaoPrestador } from '../../../../../DTOS/Prestador/CarregarAreasAtuacaoPrestador.dto';

@Component({
  selector: 'app-aba-areas',
  imports: [ProgressSpinnerModule],
  templateUrl: './aba-areas.html',
  styleUrl: './aba-areas.css',
})
export class AbaAreas implements OnInit {
  carregando = false;
  private prestadorService = inject(PrestadorService);
  carregarAreas: CarregarAreasAtuacaoPrestador | null = null;
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.carregando = true;
    this.prestadorService.carregarCategoriasPrestador().subscribe({
      next: (res) => {
        this.carregando = false;
        this.carregarAreas = res;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  adicionarCategoriaNaLista(id: number) {
    if (id === null || id <= 0) {
      return;
    }
    this.prestadorService.adicionarCategoria(id).subscribe({
      next: (res) => {
        this.prestadorService.carregarCategoriasPrestador().subscribe({
          next: (res) => {
            this.carregando = false;
            this.carregarAreas = res;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error(err);
          },
        });
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  removerCategoriaNaLista(id: number) {
    if (id === null || id <= 0) {
      return;
    }
    this.prestadorService.removerCategoria(id).subscribe({
      next: (res) => {
        this.prestadorService.carregarCategoriasPrestador().subscribe({
          next: (res) => {
            this.carregando = false;
            this.carregarAreas = res;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error(err);
          },
        });
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
