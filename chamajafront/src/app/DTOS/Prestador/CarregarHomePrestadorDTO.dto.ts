import { InteracaoIniciaInfoUteisParaPrestador } from "../InteracaoInicial/InteracaoInicialInfoUteisParaPrestador.dto";
import { ServicoSimplificadoDTO } from "../Servico/ServicoSimplificadoDTO.dto";

export interface CarregarHomePrestadorDTO{

    nome : string,
    cidade : string,
    proximoServico : ServicoSimplificadoDTO,
    solicitacoesPendentes : InteracaoIniciaInfoUteisParaPrestador []

}