package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.commom.validation.ValidUUID
import br.com.zupacademy.mariel.domain.ChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ValidPixKey
@Introspected
data class NovaChavePixDto(

    @ValidUUID
    @field:NotBlank(message = "Nao pode ser branco")
    val idCliente: String,

    @field:NotNull(message = "Nao pode ser nulo")
    val tipoChave: TipoChave?,

    @field:NotNull(message = "Nao pode ser nulo")
    val tipoConta: TipoConta?,

    val chave: String
){
    fun toEntity() : ChavePix {
        return ChavePix(
            idCliente =  idCliente,
            tipoChave = tipoChave.toString(),
            tipoConta = tipoConta.toString(),
            chave = if (tipoChave == TipoChave.CHAVE_ALEATORIA) UUID.randomUUID().toString() else chave
        )
    }
}
