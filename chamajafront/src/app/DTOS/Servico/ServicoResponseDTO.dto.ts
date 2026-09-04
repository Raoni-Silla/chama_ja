export interface ServicoResponseDTO {
    idChamado: number;
    titulo: string;
    statusChamado: string; // Certifique-se de criar este enum ou union type no TS
    idOutraPessoa: number;
    nomeOutraPessoa: string;
    fotoOutraPessoa: string;
    dataCriacao: string; // ISO 8601 string (ex: "2026-09-01T15:35:39")
    dataHoraServico: string;
    horaFinalizacao: string;
    valorServico: number;
    concluidoPeloCliente: boolean;
    concluidoPeloPrestador: boolean;
}