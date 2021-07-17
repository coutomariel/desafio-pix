package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.ChavePixRequest
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta

fun ChavePixRequest.toEntity(): NovaChavePixDto {

    return NovaChavePixDto(
        idCliente = this.idCliente,
        tipoChave = when(tipoChave){
            TipoChave.UNKNOWN_KEY_TYPE -> null
            else -> TipoChave.valueOf(tipoChave.name)
        },
        tipoConta = when(tipoConta){
            TipoConta.UNKNOWN_ACCOUNT -> null
            else -> TipoConta.valueOf(tipoConta.name)
        },
        chave = chave
    )
}
