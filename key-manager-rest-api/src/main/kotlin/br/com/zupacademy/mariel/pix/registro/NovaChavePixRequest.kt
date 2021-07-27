package br.com.zupacademy.mariel.pix.registro

import br.com.zupacademy.mariel.ChavePixRequest
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
class NovaChavePixRequest(
    @field:NotNull val tipoConta: TipoConta?,
    @field:Size(max = 77) val chave: String?,
    @field:NotNull val tipoChave: TipoChave?
) {
    fun paraModeloGrpc(clientId: UUID): ChavePixRequest {
        return ChavePixRequest.newBuilder()
            .setIdCliente(clientId.toString())
            .setTipoConta(tipoConta ?: TipoConta.UNKNOWN_ACCOUNT)
            .setTipoChave(tipoChave ?: TipoChave.UNKNOWN_KEY_TYPE)
            .setChave(chave ?: "").build()
    }
}
