import br.com.zupacademy.mariel.ChavePixToRemoveRequest
import br.com.zupacademy.mariel.pix.remove.ChavePixToRemove

fun ChavePixToRemoveRequest.toDto(): ChavePixToRemove {
    return ChavePixToRemove(
        pixId = this.pixId,
        idCliente = this.idCliente
    )
}