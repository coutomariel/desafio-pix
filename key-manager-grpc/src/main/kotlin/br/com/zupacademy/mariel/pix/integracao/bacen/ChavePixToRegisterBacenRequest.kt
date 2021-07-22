package br.com.zupacademy.mariel.pix.integracao.bacen

import br.com.zupacademy.mariel.TipoChave

data class ChavePixToRegisterBacenRequest(
    val keyType: TipoChave,
    val key: String,
    val bankAccount: BanckAccount,
    val owner: Owner,
)