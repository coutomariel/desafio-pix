package br.com.zupacademy.mariel.pix.integracao.bacen

import java.time.LocalDateTime

data class ChavePixToRemoveBacenResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)