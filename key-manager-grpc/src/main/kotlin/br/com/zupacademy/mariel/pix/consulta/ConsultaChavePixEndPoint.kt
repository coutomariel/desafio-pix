package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavePixDetailsResponse
import br.com.zupacademy.mariel.ChavePixToSearchRequest
import br.com.zupacademy.mariel.KeyManagerConsultaChavePixGrpcServiceGrpc
import br.com.zupacademy.mariel.commom.grpc.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ConsultaChavePixEndPoint(
    @Inject val service: ConsultaChavePixService
) : KeyManagerConsultaChavePixGrpcServiceGrpc.KeyManagerConsultaChavePixGrpcServiceImplBase() {

    override fun consulta(
        request: ChavePixToSearchRequest,
        responseObserver: StreamObserver<ChavePixDetailsResponse>
    ) {
        val consulta = service.consultaChavePix(request)

        responseObserver.onNext(consulta)
        responseObserver.onCompleted()

    }
}

