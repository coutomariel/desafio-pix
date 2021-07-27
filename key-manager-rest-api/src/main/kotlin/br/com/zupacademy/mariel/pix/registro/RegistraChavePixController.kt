package br.com.zupacademy.mariel.pix.registro

import br.com.zupacademy.mariel.KeyManagerRegisterGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.Valid


@Validated
@Controller("/api/v1/clientes")
class RegistraChavePixController(
    private val grpcClient: KeyManagerRegisterGrpcServiceGrpc.KeyManagerRegisterGrpcServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(RegistraChavePixController::class.java)

    @Post("/{idCliente}/pix")
    fun registra(idCliente : UUID,  @Valid @Body request : NovaChavePixRequest) : HttpResponse<Any> {

        LOGGER.info("[${idCliente}] criando uma chave pix com $request")

        val grpcResponse = grpcClient.cadastrar(request.paraModeloGrpc(idCliente))
        val location = HttpResponse.uri("/api/v1/clientes/${idCliente}/pix/${grpcResponse.pixId}")

        return HttpResponse.created(location)
    }
}