package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.ChavePixToRemoveRequest
import br.com.zupacademy.mariel.KeyManagerRemoveGrpcServiceGrpc
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.domain.ContaAssociada
import br.com.zupacademy.mariel.domain.Owner
import br.com.zupacademy.mariel.pix.integracao.erp.ErpItauClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClientZ: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub,
) {

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }


    @Test
    fun `deve remover uma chave pix a partir de seu pix id e chave correspondente`() {
        val successMessage = "Chave removida com sucesso"
        val chavePixExistente = repository.save(MockChavePixEntityToInsert())

        val chavePixToRemove = ChavePixToRemoveRequest
            .newBuilder()
            .setPixId(chavePixExistente.id.toString())
            .setIdCliente(chavePixExistente.idCliente)
            .build()

        var response = grpcClientZ.remover(chavePixToRemove)

        assertTrue(!repository.existsById(UUID.fromString(chavePixToRemove.pixId)))
        assertEquals(successMessage, response.message)

    }

    @Test
    fun `nao deve permitir a remocao de uma chave quando nao solicitada pelo dono da mesma`() {

        val messageError = "Chave pix nao encotrada para o ID c56dfef4-7901-44fb-84e2-a2cefb157890"
        val error = assertThrows<StatusRuntimeException> {
            grpcClientZ.remover(MockChavePixEntityToRemoveRequest())
        }

        with(error) {
            assertEquals(Status.NOT_FOUND.code, this.status.code)
            assertEquals(messageError, this.status.description)
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

    private fun MockChavePixEntityToRemoveRequest(): ChavePixToRemoveRequest {
        return ChavePixToRemoveRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890").build()
    }

    @MockBean(ErpItauClient::class)
    fun mockFinancialClient(): ErpItauClient {
        return mock(ErpItauClient::class.java)
    }

    @Factory
    private class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}