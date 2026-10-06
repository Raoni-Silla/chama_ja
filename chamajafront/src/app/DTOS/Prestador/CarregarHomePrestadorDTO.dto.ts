import { InteracaoIniciaInfoUteisParaPrestador } from "../InteracaoInicial/InteracaoInicialInfoUteisParaPrestador.dto";
import { ServicoSimplificadoDTO } from "../Servico/ServicoSimplificadoDTO.dto";

export interface CarregarHomePrestadorDTO{
    nome : string,
    cidade : string,
    fotoUrl : string,
    proximoServico : ServicoSimplificadoDTO,
    solicitacoesPendentes : InteracaoIniciaInfoUteisParaPrestador []
}