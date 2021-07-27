package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.ChavePixToRemoveRequest
import br.com.zupacademy.mariel.KeyManagerRemoveGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes")
class RemoveChavePixController(
    private val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(RemoveChavePixController::class.java)

    @Delete("/{clienteId}/pix/{pixId}")
    fun remove(clienteId: UUID, pixId: UUID): HttpResponse<Any> {

        LOGGER.info("Removendo chave pix de ID '${pixId}'")
        grpcClient.remover(
            ChavePixToRemoveRequest
                .newBuilder()
                .setIdCliente(clienteId.toString())
                .setPixId(pixId.toString())
                .build()
        )

        return HttpResponse.ok()
    }

}