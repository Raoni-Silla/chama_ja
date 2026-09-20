import { EnderecoResponseDTO } from "../Endereco/EnderecoResponseDTO.dto";

export interface InteracaoInicialResponseDTO {
    id : number,
    titulo : string,
    descricao : string,
    valorSugerido : number,
    dataCriacao : string,
    statusInteracao : string,
    endereco : EnderecoResponseDTO
}