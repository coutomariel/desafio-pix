package br.com.zupacademy.mariel.pix.integracao.bacen

import br.com.zupacademy.mariel.TipoChave
import java.time.LocalDateTime

data class ChavePixToRegisterBacenResponse(
    val keyType: TipoChave,
    val key: String,
    val bankAccount: BanckAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)