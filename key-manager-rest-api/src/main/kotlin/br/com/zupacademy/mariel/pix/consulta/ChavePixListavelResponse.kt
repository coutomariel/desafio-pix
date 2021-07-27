package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavesDeUmClienteResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChavePixListavelResponse(chavePix: ChavesDeUmClienteResponse.Chave) {
    val id = chavePix.pixId
    val chave = chavePix.chave
    val tipoDeConta = chavePix.tipoConta
    val tipo = chavePix.tipo
    val criadaEm = chavePix.criadoEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

}
