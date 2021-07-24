package br.com.zupacademy.mariel.pix.lista

import br.com.zupacademy.mariel.ChavesDeUmClienteRequest
import br.com.zupacademy.mariel.KeyManagerListaChavesDeUmClienteGrpcServiceGrpc
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.domain.ContaAssociada
import br.com.zupacademy.mariel.domain.Owner
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class ListaChavesDeUmClienteEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerListaChavesDeUmClienteGrpcServiceGrpc.KeyManagerListaChavesDeUmClienteGrpcServiceBlockingStub
) {

    companion object {
        val CLIENT_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setUp() {
        repository.save(createFakeChavePixEntityToInsert(tipoChave = "CHAVE_ALEATORIA"))
        repository.save(
            createFakeChavePixEntityToInsert(
                tipoChave = "EMAIL",
                chave = "fulano@gmail.com",
                idCliente = CLIENT_ID.toString()
            )
        )
        repository.save(
            createFakeChavePixEntityToInsert(
                tipoChave = "CPF",
                chave = "05838416785",
                idCliente = CLIENT_ID.toString()
            )
        )
    }

    @AfterEach
    fun clear() {
        repository.deleteAll()
    }

    @Test
    fun `deve listar todas as chaves de um cliente`() {
        val response = grpcClient.lista(
            ChavesDeUmClienteRequest
                .newBuilder().setClientId(CLIENT_ID.toString()).build()
        )

        with(response) {
            assertEquals(2, chavesCount)
            assertThat(
                this.chavesList.map { Pair(it.tipo, it.chave) }.toList(),
                containsInAnyOrder(
                    Pair(TipoChave.EMAIL.toString(), "fulano@gmail.com"),
                    Pair(TipoChave.CPF.toString(), "05838416785")
                )
            )
        }
    }


    @Test
    fun `nao deve listar as chaves quando cliente nao possuir chaves`() {
        val response = grpcClient.lista(
            ChavesDeUmClienteRequest
                .newBuilder().setClientId(UUID.randomUUID().toString()).build()
        )

        assertTrue(response.chavesList.isEmpty())
        assertEquals(0, response.chavesCount)
    }

    @Test
    fun `nao deve listar as chaves quando um clienteId for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.lista(
                ChavesDeUmClienteRequest.newBuilder().setClientId("").build()
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("ID do cliente e um parametro obrigatorio e precisa ser valido", status.description)
        }


    }

    private fun createFakeChavePixEntityToInsert(
        idCliente: String = "c56dfef4-7901-44fb-84e2-a2cefb157890",
        tipoChave: String = TipoChave.CPF.toString(),
        tipoConta: String = TipoConta.CONTA_CORRENTE.toString(),
        chave: String = "82564475590",
        contaAssociada: ContaAssociada = ContaAssociada(numero = "50515", tipo = TipoConta.CONTA_CORRENTE.toString()),
        owner: Owner = Owner(nome = "Brandon Stark", cpf = "96543443430")
    ): ChavePix {
        return ChavePix(
            idCliente = idCliente,
            tipoChave = tipoChave,
            tipoConta = tipoConta,
            chave = chave,
            contaAssociada = contaAssociada,
            owner = owner
        )
    }

    @Factory
    private class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                KeyManagerListaChavesDeUmClienteGrpcServiceGrpc.KeyManagerListaChavesDeUmClienteGrpcServiceBlockingStub {
            return KeyManagerListaChavesDeUmClienteGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}