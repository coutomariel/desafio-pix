package br.com.zupacademy.mariel.pix.integracao.bacen

data class ChavePixToRemoveBacenRequest(
    val key: String,
    val participant: String
)