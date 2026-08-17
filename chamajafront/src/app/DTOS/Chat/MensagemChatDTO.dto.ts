export interface MensagemChatDTO {
    idMensagem : number,
    idChamado : number,
    idRemetente : number,
    nomeRemetente : string,
    fotoRemetente : string,
    conteudo : string,
    dataEnvio : string,
    lida : boolean,
    minhaMensagem : boolean
}