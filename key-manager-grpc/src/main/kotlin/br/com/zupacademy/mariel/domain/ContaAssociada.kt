package br.com.zupacademy.mariel.domain

import javax.persistence.Embeddable

@Embeddable
class ContaAssociada(
    val numero: String,
    val tipo: String
)