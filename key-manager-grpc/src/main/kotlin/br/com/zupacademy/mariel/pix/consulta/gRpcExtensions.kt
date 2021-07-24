package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavePixDetailsResponse
import br.com.zupacademy.mariel.ChavePixToSearchRequest
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.pix.integracao.bacen.BanckAccount
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixByChaveBacenResponse
import br.com.zupacademy.mariel.pix.integracao.bacen.TipoContaBacen
import com.google.protobuf.Timestamp

fun ChavePixToSearchRequest.getSearchType(): TipoPesquisa {
    return if (chave.isNullOrBlank()) {
        TipoPesquisa.POR_PIX_ID
    } else {
        TipoPesquisa.POR_CHAVE
    }
}

fun ChavePix.toChavePixDetailsResponse(): ChavePixDetailsResponse? {
    return ChavePixDetailsResponse
        .newBuilder()
        .setChavePix(
            ChavePixDetailsResponse.ChavePix
                .newBuilder()
                .setTipo(TipoChave.valueOf(tipoChave))
                .setChave(chave)
                .setCriadaEm(Timestamp.newBuilder().setSeconds(criadaEm.second.toLong()).setNanos(criadaEm.nano).build())
                .setConta(
                    ChavePixDetailsResponse.ChavePix.ContaInfo
                        .newBuilder()
                        .setInstituicao(BanckAccount.getParticipant())
                        .setAgencia(BanckAccount.getBranch())
                        .setNumeroDeConta(contaAssociada.numero)
                        .setTipo(TipoConta.valueOf(contaAssociada.tipo))
                        .setCpfDoTitular(owner.cpf)
                        .setNomeDoTitular(owner.nome)
                        .build()
                )
                .build()
        )
        .setClientId(idCliente)
        .setPixId(id.toString())
        .build()
}

fun ChavePixByChaveBacenResponse.toChavePixDetailsResponse(): ChavePixDetailsResponse? {
    return ChavePixDetailsResponse
        .newBuilder()
        .setChavePix(
            ChavePixDetailsResponse.ChavePix
                .newBuilder()
                .setTipo(TipoChave.valueOf(keyType.toString()))
                .setChave(key)
                .setCriadaEm(Timestamp.newBuilder().setSeconds(createdAt.second.toLong()).setNanos(createdAt.nano).build())
                .setConta(
                    ChavePixDetailsResponse.ChavePix.ContaInfo
                        .newBuilder()
                        .setInstituicao(bankAccount.participant)
                        .setAgencia(bankAccount.branch)
                        .setNumeroDeConta(bankAccount.accountNumber)
                        .setTipo(TipoContaBacen.valueOf(bankAccount.accountType).convert())
                        .setCpfDoTitular(owner.taxIdNumber)
                        .setNomeDoTitular(owner.name)
                        .build()
                ).build()
        )
        .build()
}

