package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.ChavePixRequest
import br.com.zupacademy.mariel.ChavePixResponse
import br.com.zupacademy.mariel.KeyManagerGrpcServiceGrpc
import br.com.zupacademy.mariel.commom.grpc.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ErrorHandler
class CadastroNovaChavePixEndPoint(
    @Inject val service: CadastroNovaChavePixService
) :
    KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun cadastrar(request: ChavePixRequest, responseObserver: StreamObserver<ChavePixResponse>?) {

        val registeredPix = service.registra(request.toDto())

        responseObserver?.onNext(ChavePixResponse.newBuilder()
            .setPixId(registeredPix?.id.toString())
            .setChave(registeredPix?.chave)
            .build())
        responseObserver?.onCompleted()

    }
}

