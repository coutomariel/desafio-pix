package br.com.zupacademy.mariel.domain

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class ChavePix(

    @Column(nullable = false)
    val idCliente: String,
    @Column(nullable = false)
    val tipoChave: String,
    @Column(nullable = false)
    val tipoConta: String,
    @Column(nullable = false)
    val chave: String,
    val contaAssociada: ContaAssociada,
    val owner: Owner,
    val criadaEm : LocalDateTime = LocalDateTime.now()
) {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null
}