package br.com.zupacademy.mariel.domain

import br.com.zupacademy.mariel.commom.validation.ValidUUID
import br.com.zupacademy.mariel.pix.registra.ValidPixKey
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
@ValidPixKey
class ChavePix(

    @ValidUUID @field:NotBlank val idCliente: String,
    @field:NotBlank val tipoChave: String,
    @field:NotBlank val tipoConta: String,
    @field:NotBlank val chave: String
) {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    val id: UUID? = null
}