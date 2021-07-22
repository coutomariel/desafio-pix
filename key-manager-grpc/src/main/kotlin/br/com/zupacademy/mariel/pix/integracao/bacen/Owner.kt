package br.com.zupacademy.mariel.pix.integracao.bacen

data class Owner(
    val name: String,
    val taxIdNumber: String,
    val type: String = "NATURAL_PERSON",
)