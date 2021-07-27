package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavesDeUmClienteRequest
import br.com.zupacademy.mariel.KeyManagerListaChavesDeUmClienteGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes")
class ListaConsultaDetalhesChavePix(
    private val grpcClient: KeyManagerListaChavesDeUmClienteGrpcServiceGrpc.KeyManagerListaChavesDeUmClienteGrpcServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/{clienteId}/pix")
    fun lista(clienteId: UUID): HttpResponse<Any> {

        val grpcResponse =
            grpcClient.lista(ChavesDeUmClienteRequest.newBuilder().setClientId(clienteId.toString()).build())

        val chaves = grpcResponse.chavesList.map { ChavePixListavelResponse(it) }

        return HttpResponse.ok(chaves)

    }
}