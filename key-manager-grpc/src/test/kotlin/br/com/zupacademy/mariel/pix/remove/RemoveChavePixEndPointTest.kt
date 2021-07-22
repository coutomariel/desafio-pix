package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.ChavePixToRemoveRequest
import br.com.zupacademy.mariel.KeyManagerRemoveGrpcServiceGrpc
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.domain.ContaAssociada
import br.com.zupacademy.mariel.domain.Owner
import br.com.zupacademy.mariel.pix.integracao.bacen.BacenClient
import br.com.zupacademy.mariel.pix.integracao.bacen.BanckAccount
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixToRemoveBacenRequest
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixToRemoveBacenResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub,
) {

    @Inject
    lateinit var bacenClient: BacenClient

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }


    @Test
    fun `deve remover uma chave pix a partir de seu pix id e chave correspondente`() {

        val chavePixExistente = repository.save(MockChavePixEntityToInsert())
        val chavePixEntityToRemoveRequest = CreateFakeChavePixToRemoveRequest(
            idCliente = chavePixExistente.idCliente,
            pixId = chavePixExistente.id.toString()
        )

        val chavePixToRemoveBacenRequest =
            ChavePixToRemoveBacenRequest(chavePixExistente.chave, BanckAccount.getParticipant())

        val chavePixToRemoveBacenResponse = ChavePixToRemoveBacenResponse(
            key = chavePixExistente.chave, participant = BanckAccount.getParticipant(), deletedAt = LocalDateTime.now()
        )

        Mockito.`when`(bacenClient.remove(chavePixToRemoveBacenRequest, chavePixExistente.chave))
            .thenReturn(HttpResponse.ok(chavePixToRemoveBacenResponse))

        val successMessage = "Chave removida com sucesso"

        var response = grpcClient.remover(chavePixEntityToRemoveRequest)

        assertTrue(!repository.existsById(UUID.fromString(chavePixEntityToRemoveRequest.pixId)))
        assertEquals(successMessage, response.message)

    }

    @Test
    fun `nao deve permitir a remocao de uma chave quando nao solicitada pelo dono da mesma`() {

        val chavePixEntityToRemoveRequest = CreateFakeChavePixToRemoveRequest()
        val messageError = "Chave pix nao encotrada para o ID ${chavePixEntityToRemoveRequest.pixId}"
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.remover(chavePixEntityToRemoveRequest)
        }

        with(error) {
            assertEquals(Status.NOT_FOUND.code, this.status.code)
            assertEquals(messageError, this.status.description)
        }
    }

    @Test
    fun `nao deve permitir a exclusao de uma chave pix quando houver falha na comunicacao com bacen`() {

        val chavePixExistente = repository.save(MockChavePixEntityToInsert())
        val chavePixEntityToRemoveRequest = CreateFakeChavePixToRemoveRequest(
            idCliente = chavePixExistente.idCliente,
            pixId = chavePixExistente.id.toString()
        )

        val chavePixToRemoveBacenRequest =
            ChavePixToRemoveBacenRequest(chavePixExistente.chave, BanckAccount.getParticipant())
        Mockito.`when`(bacenClient.remove(chavePixToRemoveBacenRequest, chavePixExistente.chave))
            .thenReturn(HttpResponse.unprocessableEntity())

        val messageError = "Problema inesperado na tentativa de exluir a chave"
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.remover(chavePixEntityToRemoveRequest)
        }

        with(error) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(messageError, status.description)
        }

    }

    private fun MockChavePixEntityToInsert(): ChavePix {
        return ChavePix(
            idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoChave.CPF.toString(),
            tipoConta = TipoConta.CONTA_CORRENTE.toString(),
            chave = "82564475590",
            contaAssociada = ContaAssociada(numero = "50515", tipo = TipoConta.CONTA_CORRENTE.toString()),
            owner = Owner(nome = "Brandon Stark", cpf = "96543443430")
        )
    }

    private fun CreateFakeChavePixToRemoveRequest(
        idCliente: String? = "c56dfef4-7901-44fb-84e2-a2cefb157890",
        pixId: String? = "c56dfef4-7901-44fb-84e2-a2cefb157890"
    ): ChavePixToRemoveRequest {

        return ChavePixToRemoveRequest.newBuilder()
            .setIdCliente(idCliente)
            .setPixId(pixId).build()
    }

    private fun CreateFakeChavePixToRemoveResponse(): ChavePixToRemoveRequest {
        return ChavePixToRemoveRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890").build()
    }

    @MockBean(BacenClient::class)
    fun mockBacenClient(): BacenClient {
        return mock(BacenClient::class.java)
    }

    @Factory
    private class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}