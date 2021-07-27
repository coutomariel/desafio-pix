package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavePixDetailsResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class DetalhesChavePixResponse(chaveResponse: ChavePixDetailsResponse) {

    val pixId = chaveResponse.pixId
    val tipo = chaveResponse.chavePix.tipo
    val chave = chaveResponse.chavePix.chave

    val criadaEm = chaveResponse.chavePix.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    val tipoConta = chaveResponse.chavePix.conta.tipo

    val conta = mapOf(
        Pair("instituicao", chaveResponse.chavePix.conta.instituicao),
        Pair("nomeDoTitular", chaveResponse.chavePix.conta.nomeDoTitular),
        Pair("cpfDoTitular", chaveResponse.chavePix.conta.cpfDoTitular),
        Pair("agencia", chaveResponse.chavePix.conta.agencia),
        Pair("numero", chaveResponse.chavePix.conta.numeroDeConta),
    )

}
