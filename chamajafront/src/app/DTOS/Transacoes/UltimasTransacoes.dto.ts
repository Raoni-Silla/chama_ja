export interface UltimasTransacoesDTO {
  valor: number;
  tipoTransacao: string; 
  data: string; // ISO 8601 string (LocalDateTime do Java vira string no JSON)
  descricao: string;
}