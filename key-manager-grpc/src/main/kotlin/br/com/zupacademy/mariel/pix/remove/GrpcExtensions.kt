import br.com.zupacademy.mariel.ChavePixToRemoveRequest
import br.com.zupacademy.mariel.pix.remove.ChavePixToRemoveDto

fun ChavePixToRemoveRequest.toDto(): ChavePixToRemoveDto {
    return ChavePixToRemoveDto(
        pixId = this.pixId,
        idCliente = this.idCliente
    )
}