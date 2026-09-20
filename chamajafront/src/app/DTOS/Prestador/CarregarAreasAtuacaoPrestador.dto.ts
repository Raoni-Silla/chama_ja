import { CategoriaDetalhesDTO } from '../Categoria/CategoriaDetalhesDTO.dto';

export interface CarregarAreasAtuacaoPrestador {
  listaCategoriasPrestador: CategoriaDetalhesDTO[];
  categoriasDisponiveis: CategoriaDetalhesDTO[];
}
