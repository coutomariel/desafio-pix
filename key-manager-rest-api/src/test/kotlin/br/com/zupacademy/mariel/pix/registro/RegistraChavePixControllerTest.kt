package br.com.zupacademy.mariel.pix.registro

import br.com.zupacademy.mariel.KeyManagerRegisterGrpcServiceGrpc
import br.com.zupacademy.mariel.pix.commom.grpc.KeyManagerGrpcFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

internal class RegistraChavePixControllerTest {

    @field:Inject
    lateinit var registraStub: KeyManagerRegisterGrpcServiceGrpc.KeyManagerRegisterGrpcServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client : HttpClient


}

@Factory
@Replaces(KeyManagerGrpcFactory::class)
internal class MockitoStubFactory {
    @Singleton
    fun stubMock() = Mockito.mock(KeyManagerRegisterGrpcServiceGrpc.KeyManagerRegisterGrpcServiceBlockingStub::class.java)
}