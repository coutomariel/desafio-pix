package br.com.zupacademy.mariel.pix.integracao.erp

import br.com.zupacademy.mariel.TipoConta

data class DetalhesContaResponse(
    val agencia: String,
    val numero: String,
    val tipo: TipoConta,
    val titular: Titular
)