package br.com.zupacademy.mariel.pix.integracao.bacen

data class BanckAccount(
    val participant: String,
    val branch: String = "0001",
    val accountNumber : String,
    val accountType: String = "CACC",
)