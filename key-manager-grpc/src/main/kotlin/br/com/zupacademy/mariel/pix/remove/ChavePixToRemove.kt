package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.commom.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ChavePixToRemove(
    @ValidUUID  @field:NotBlank val pixId: String = "",
    @field:NotBlank val idCliente: String = ""
)
