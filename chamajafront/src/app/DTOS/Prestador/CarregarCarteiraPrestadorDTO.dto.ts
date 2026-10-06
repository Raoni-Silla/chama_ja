import { UltimosPagamentosDTO } from "../Pagamento/UltimosPagamentos.DTO";
import { UltimasTransacoesDTO } from "../Transacoes/UltimasTransacoes.dto";

export interface CarregarCarteiraPrestadorDTO {
  saldoDisponivel: number;
  chavePix: string;
  ultimosPagamentos: UltimosPagamentosDTO[];
  ultimasTransacoes: UltimasTransacoesDTO[];
}