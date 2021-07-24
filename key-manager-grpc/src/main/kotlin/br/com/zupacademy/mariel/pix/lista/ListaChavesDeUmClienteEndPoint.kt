package br.com.zupacademy.mariel.pix.lista

import br.com.zupacademy.mariel.ChavesDeUmClienteRequest
import br.com.zupacademy.mariel.ChavesDeUmClienteResponse
import br.com.zupacademy.mariel.KeyManagerListaChavesDeUmClienteGrpcServiceGrpc
import br.com.zupacademy.mariel.commom.grpc.handlers.ErrorHandler
import br.com.zupacademy.mariel.domain.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ErrorHandler
class ListaChavesDeUmClienteEndPoint(
    @Inject val repository: ChavePixRepository
) : KeyManagerListaChavesDeUmClienteGrpcServiceGrpc.KeyManagerListaChavesDeUmClienteGrpcServiceImplBase() {

    override fun lista(
        request: ChavesDeUmClienteRequest,
        responseObserver: StreamObserver<ChavesDeUmClienteResponse>?
    ) {

        if (request.clientId.isNullOrBlank()) {
            throw IllegalArgumentException("ID do cliente e um parametro obrigatorio e precisa ser valido")
        }

        val chaves = repository.findByIdCliente(request.clientId)
            .map { chave ->
                ChavesDeUmClienteResponse.Chave
                    .newBuilder()
                    .setPixId(chave.id.toString())
                    .setTipo(chave.tipoChave)
                    .setChave(chave.chave)
                    .setTipoConta(chave.contaAssociada.tipo)
                    .setCriadoEm(
                        Timestamp.newBuilder()
                            .setSeconds(chave.criadaEm.second.toLong()).setNanos(chave.criadaEm.nano)
                    )
                    .build()
            }

        responseObserver?.onNext(
            ChavesDeUmClienteResponse
                .newBuilder()
                .setClientId(request.clientId.toString())
                .addAllChaves(chaves)
                .build()
        )

        responseObserver?.onCompleted()
    }

}