export interface PrestadorResponseDTO {
  id: number;
  nome: string;
  fotoUrl: string;
  biografia: string;
  notaMedia: number;
  valorHora: number;
  cidadePrincipal: string;
  categorias: string[];
  distanciaKm : number
}