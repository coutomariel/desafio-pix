package br.com.zupacademy.mariel.domain

import javax.persistence.Embeddable

@Embeddable
class Owner(
    val nome: String,
    val cpf: String
)