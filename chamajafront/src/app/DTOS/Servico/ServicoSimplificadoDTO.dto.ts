import { EnderecoResponseDTO } from "../Endereco/EnderecoResponseDTO.dto";

export interface ServicoSimplificadoDTO {
    idChamado: number;
    titulo: string;
    statusChamado: string;
    idOutraPessoa: number;
    nomeOutraPessoa: string;
    fotoOutraPessoa: string;
    dataHoraServico: Date | string; // Use Date se converter no front, ou string se vier como ISO do JSON
    valorServico: number;
    endereco: EnderecoResponseDTO;
}