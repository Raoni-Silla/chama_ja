export interface MelhoresDoMesDTO{
    id: number,
    nome: string,
    urlFoto: string,
   categorias: string[],
    notaMedia: number,
    isVerificado: boolean,
    quantidadeAvaliacoes: number,
    distanciaKm : number
}