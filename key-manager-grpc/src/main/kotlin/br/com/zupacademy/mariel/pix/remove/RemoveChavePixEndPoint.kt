package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.ChavePixToRemoveRequest
import br.com.zupacademy.mariel.ChavePixToRemoveResponse
import br.com.zupacademy.mariel.KeyManagerRemoveGrpcServiceGrpc
import br.com.zupacademy.mariel.commom.grpc.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import toDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChavePixEndPoint(@Inject val service: RemoveChavePixService) :
    KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase() {

    override fun remover(
        request: ChavePixToRemoveRequest,
        responseObserver: StreamObserver<ChavePixToRemoveResponse>?
    ) {

        service.remover(request.toDto())

        responseObserver?.onNext(
            ChavePixToRemoveResponse
                .newBuilder()
                .setMessage("Chave removida com sucesso")
                .build()
        )

        responseObserver?.onCompleted()
    }
}


