package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavePixToSearchRequest
import br.com.zupacademy.mariel.KeyManagerConsultaChavePixGrpcServiceGrpc
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.domain.ContaAssociada
import br.com.zupacademy.mariel.domain.Owner
import br.com.zupacademy.mariel.pix.integracao.bacen.BacenClient
import br.com.zupacademy.mariel.pix.integracao.bacen.BanckAccount
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixByChaveBacenResponse
import br.com.zupacademy.mariel.pix.integracao.bacen.TipoContaBacen
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import br.com.zupacademy.mariel.pix.integracao.bacen.Owner as OwnerFromBacen

@MicronautTest(transactional = false)
internal class ConsultaChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: KeyManagerConsultaChavePixGrpcServiceGrpc.KeyManagerConsultaChavePixGrpcServiceBlockingStub
) {
    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Inject
    lateinit var bacenClient: BacenClient

    @Test
    fun `deve retornar umerro quando chave no banco nem bacen`() {

        val RANDOM_KEY = UUID.randomUUID().toString()
        val messageError = "Chave pix nao encotrada com os parametros informados"

        `when`(bacenClient.consulta(RANDOM_KEY))
            .thenReturn(HttpResponse.badRequest())


        val request = ChavePixToSearchRequest.newBuilder().setChave(RANDOM_KEY).build()


        val error = assertThrows<StatusRuntimeException> {
            grpcClient.consulta(request)
        }

        with(error) {
            Assertions.assertEquals(Status.NOT_FOUND.code, this.status.code)
            Assertions.assertEquals(messageError, this.status.description)
        }

    }


    @Test
    fun `deve retornar uma chave pix a partir de uma chave existente apenas no bacen`() {

        val RANDOM_KEY = UUID.randomUUID().toString()
        val chavePixFromBacenResponse = createFakeChavePixResponseFromBacenClient(RANDOM_KEY)

        `when`(bacenClient.consulta(RANDOM_KEY))
            .thenReturn(HttpResponse.ok(chavePixFromBacenResponse))


        val request = ChavePixToSearchRequest.newBuilder().setChave(RANDOM_KEY).build()


        val response = grpcClient.consulta(request)

        with(response) {
            Assertions.assertEquals(chavePixFromBacenResponse.owner.name, this.chavePix.conta.nomeDoTitular)
            Assertions.assertEquals(chavePixFromBacenResponse.owner.taxIdNumber, this.chavePix.conta.cpfDoTitular)
            Assertions.assertEquals(
                chavePixFromBacenResponse.bankAccount.accountNumber,
                this.chavePix.conta.numeroDeConta
            )
            Assertions.assertEquals(
                TipoConta.valueOf(chavePixFromBacenResponse.bankAccount.accountType),
                this.chavePix.conta.tipo
            )
            Assertions.assertEquals(chavePixFromBacenResponse.keyType, this.chavePix.tipo)
            Assertions.assertEquals(chavePixFromBacenResponse.key, this.chavePix.chave)
        }

    }

    private fun createFakeChavePixResponseFromBacenClient(RANDOM_KEY: String) = ChavePixByChaveBacenResponse(
        keyType = TipoChave.CHAVE_ALEATORIA,
        key = RANDOM_KEY,
        bankAccount = BanckAccount(
            participant = BanckAccount.getParticipant(),
            branch = BanckAccount.getBranch(),
            accountNumber = "515099",
            accountType = TipoContaBacen.CACC.toString()
        ),
        owner = OwnerFromBacen(
            name = "Elon Musk",
            taxIdNumber = "51541882577",
        ),
        createdAt = LocalDateTime.now()
    )


    @Test
    fun `deve retornar uma chave pix a partir de uma chave existente na base`() {

        val chaveExistente = repository.save(CreateFakeChavePixEntityToInsert())
        val request = ChavePixToSearchRequest.newBuilder().setChave(chaveExistente.chave).build()
        val response = grpcClient.consulta(request)

        with(response) {
            Assertions.assertEquals(chaveExistente.owner.nome, this.chavePix.conta.nomeDoTitular)
            Assertions.assertEquals(chaveExistente.owner.cpf, this.chavePix.conta.cpfDoTitular)
            Assertions.assertEquals(chaveExistente.contaAssociada.numero, this.chavePix.conta.numeroDeConta)
            Assertions.assertEquals(TipoConta.valueOf(chaveExistente.contaAssociada.tipo), this.chavePix.conta.tipo)
            Assertions.assertEquals(TipoChave.valueOf(chaveExistente.tipoChave), this.chavePix.tipo)
            Assertions.assertEquals(chaveExistente.chave, this.chavePix.chave)
        }

    }

    @Test
    fun `deve retornar uma chave pix a partir de uma pixId na base`() {

        val chaveExistente = repository.save(CreateFakeChavePixEntityToInsert())

        val request = ChavePixToSearchRequest
            .newBuilder()
            .setFiltroPorPixId(
                ChavePixToSearchRequest
                    .Filtro.newBuilder()
                    .setIdCliente(chaveExistente.idCliente).setPixId(chaveExistente.id.toString())
            )
            .build()

        val response = grpcClient.consulta(request)

        with(response) {
            Assertions.assertEquals(chaveExistente.owner.nome, this.chavePix.conta.nomeDoTitular)
            Assertions.assertEquals(chaveExistente.owner.cpf, this.chavePix.conta.cpfDoTitular)
            Assertions.assertEquals(chaveExistente.contaAssociada.numero, this.chavePix.conta.numeroDeConta)
            Assertions.assertEquals(TipoConta.valueOf(chaveExistente.contaAssociada.tipo), this.chavePix.conta.tipo)
            Assertions.assertEquals(TipoChave.valueOf(chaveExistente.tipoChave), this.chavePix.tipo)
            Assertions.assertEquals(chaveExistente.chave, this.chavePix.chave)
        }

    }


    private fun CreateFakeChavePixEntityToInsert(): ChavePix {
        return ChavePix(
            idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoChave.CPF.toString(),
            tipoConta = TipoConta.CONTA_CORRENTE.toString(),
            chave = "82564475590",
            contaAssociada = ContaAssociada(numero = "50515", tipo = TipoConta.CONTA_CORRENTE.toString()),
            owner = Owner(nome = "Brandon Stark", cpf = "96543443430")
        )
    }

    @MockBean(BacenClient::class)
    fun mockBacenClient(): BacenClient {
        return Mockito.mock(BacenClient::class.java)
    }

    @Factory
    private class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                KeyManagerConsultaChavePixGrpcServiceGrpc.KeyManagerConsultaChavePixGrpcServiceBlockingStub {
            return KeyManagerConsultaChavePixGrpcServiceGrpc.newBlockingStub(channel)
        }
    }


}