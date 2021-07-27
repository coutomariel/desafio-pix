package br.com.zupacademy.mariel.pix.commom.grpc

import br.com.zupacademy.mariel.KeyManagerConsultaChavePixGrpcServiceGrpc
import br.com.zupacademy.mariel.KeyManagerRegisterGrpcServiceGrpc
import br.com.zupacademy.mariel.KeyManagerRemoveGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGrpcFactory(
    @GrpcChannel("keyManagerGrpc") val channel: ManagedChannel
) {

    @Singleton
    fun registraChave() = KeyManagerRegisterGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultaChave() = KeyManagerConsultaChavePixGrpcServiceGrpc.newBlockingStub(channel)
}