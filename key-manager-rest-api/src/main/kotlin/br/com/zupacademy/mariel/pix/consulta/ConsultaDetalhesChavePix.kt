package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavePixToSearchRequest
import br.com.zupacademy.mariel.KeyManagerConsultaChavePixGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes")
class ConsultaDetalhesChavePix(
    private val clietGrpc : KeyManagerConsultaChavePixGrpcServiceGrpc.KeyManagerConsultaChavePixGrpcServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/{clienteId}/pix/{pixId}")
    fun consulta(clienteId: UUID, pixId: UUID): HttpResponse<Any> {

        LOGGER.info("Exibindo detalhes da chave pix de ID: '${pixId}'")

        val filtro = ChavePixToSearchRequest.Filtro
            .newBuilder()
            .setIdCliente(clienteId.toString())
            .setPixId(pixId.toString())
            .build()

        val chaveResponse = clietGrpc.consulta(ChavePixToSearchRequest.newBuilder().setFiltroPorPixId(filtro).build())

        return HttpResponse.ok(DetalhesChavePixResponse(chaveResponse))
    }
}