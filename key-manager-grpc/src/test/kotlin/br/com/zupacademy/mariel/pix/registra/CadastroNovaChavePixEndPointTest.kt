package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.ChavePixRequest
import br.com.zupacademy.mariel.KeyManagerRegisterGrpcServiceGrpc
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.domain.ContaAssociada
import br.com.zupacademy.mariel.domain.Owner
import br.com.zupacademy.mariel.pix.integracao.bacen.BacenClient
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixToRegisterBacenResponse
import br.com.zupacademy.mariel.pix.integracao.erp.DetalhesContaResponse
import br.com.zupacademy.mariel.pix.integracao.erp.ErpItauClient
import br.com.zupacademy.mariel.pix.integracao.erp.Titular
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class CadastroNovaChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: KeyManagerRegisterGrpcServiceGrpc.KeyManagerRegisterGrpcServiceBlockingStub,
) {

    @Inject
    lateinit var erpItauClient: ErpItauClient

    @Inject
    lateinit var bacenClient: BacenClient

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave pix a partir de um cpf valido`() {

        // Cenário
        val chavePixToSave = createFakeChavePixRequestToInsert(tipoChave = TipoChave.CPF, chave = "82564475590")

        val fakeBodyResponseErpItauClient = createFakeResponseForErpItauClient()

        `when`(erpItauClient.consultaClientePeloId(chavePixToSave.idCliente, chavePixToSave.tipoConta))
            .thenReturn(HttpResponse.ok(fakeBodyResponseErpItauClient))

        val bacenRequest = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient)
        val fakeBodyResponseBacenClient =
            createFakeResponseForBacenClient(chavePixToSave, fakeBodyResponseErpItauClient)

        `when`(bacenClient.registra(bacenRequest))
            .thenReturn(HttpResponse.created(fakeBodyResponseBacenClient))

        // Ação
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        // Validação
        with(chavePixResponse) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsById(UUID.fromString(this.pixId)))
        }

        val chavePixInDatabase = repository.findById(UUID.fromString(chavePixResponse.pixId)).get()
        with(chavePixInDatabase) {
            assertEquals(chavePixToSave.tipoChave.toString(), tipoChave)
            assertEquals(chavePixToSave.tipoConta.toString(), tipoConta)
            assertEquals(chavePixToSave.idCliente, idCliente)
            assertNotNull(id)
        }

    }

    @Test
    fun `deve registrar uma nova chave pix a partir de um celular valido`() {

        //Cenário
        val chavePixToSave = createFakeChavePixRequestToInsert(
            tipoChave = TipoChave.TELEFONE_CELULAR, chave = "+5585988714077"
        )

        val fakeBodyResponseErpItauClient = createFakeResponseForErpItauClient()
        `when`(erpItauClient.consultaClientePeloId(chavePixToSave.idCliente, chavePixToSave.tipoConta))
            .thenReturn(HttpResponse.ok(fakeBodyResponseErpItauClient))


        val bacenRequest = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient)
        val fakeBodyResponseBacenClient =
            createFakeResponseForBacenClient(chavePixToSave, fakeBodyResponseErpItauClient)

        `when`(bacenClient.registra(bacenRequest))
            .thenReturn(HttpResponse.created(fakeBodyResponseBacenClient))

        //Ação
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        // Validação
        with(chavePixResponse) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsById(UUID.fromString(this.pixId)))
        }

        val chavePixInDatabase = repository.findById(UUID.fromString(chavePixResponse.pixId)).get()
        with(chavePixInDatabase) {
            assertEquals(chavePixToSave.tipoChave.toString(), tipoChave)
            assertEquals(chavePixToSave.tipoConta.toString(), tipoConta)
            assertEquals(chavePixToSave.idCliente, idCliente)
            assertNotNull(id)
        }

    }

    @Test
    fun `deve registrar uma nova chave pix a partir de um email valido`() {

        // Cenário
        val chavePixToSave = createFakeChavePixRequestToInsert(
            tipoChave = TipoChave.EMAIL,
            chave = "emailquentucho@gmail.com"
        )

        val fakeBodyResponseErpItauClient = createFakeResponseForErpItauClient()
        `when`(erpItauClient.consultaClientePeloId(chavePixToSave.idCliente, chavePixToSave.tipoConta))
            .thenReturn(HttpResponse.ok(fakeBodyResponseErpItauClient))


        val bacenRequest = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient)
        val fakeBodyResponseBacenClient =
            createFakeResponseForBacenClient(chavePixToSave, fakeBodyResponseErpItauClient)

        `when`(bacenClient.registra(bacenRequest))
            .thenReturn(HttpResponse.created(fakeBodyResponseBacenClient))

        //Ação
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        with(chavePixResponse) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsById(UUID.fromString(this.pixId)))
        }

        //Validação
        val chavePixInDatabase = repository.findById(UUID.fromString(chavePixResponse.pixId)).get()
        with(chavePixInDatabase) {
            assertEquals(chavePixToSave.tipoChave.toString(), tipoChave)
            assertEquals(chavePixToSave.tipoConta.toString(), tipoConta)
            assertEquals(chavePixToSave.idCliente, idCliente)
            assertNotNull(id)
        }

    }

    @Test
    fun `nao deve adicionar uma chave ja existente`() {

        // Cenário
        val fakeBodyResponseErpItauClient = createFakeResponseForErpItauClient()
        val existente = repository.save(
            createFakeChavePixEntityToInsert(
                contaAssociada = ContaAssociada(
                    numero = fakeBodyResponseErpItauClient.numero,
                    tipo = fakeBodyResponseErpItauClient.tipo.toString()
                ),
                owner = Owner(
                    nome = fakeBodyResponseErpItauClient.titular.nome,
                    cpf = fakeBodyResponseErpItauClient.titular.cpf
                )
            )
        )

        //Ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(createFakeChavePixRequestToInsert())
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Chave Pix ${existente.chave} existente", this.status.description)
        }

    }

    @Test
    fun `nao deve adicionar nova chave pix com tipo de conta invalido`() {
        val chavePixRequestToInsert = createFakeChavePixRequestToInsert(tipoConta = TipoConta.UNKNOWN_ACCOUNT)

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(chavePixRequestToInsert)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados de entrada invalidos", this.status.description)
        }
    }

    @Test
    fun `nao deve adicionar nova chave pix com tipo de chave invalido`() {
        val chavePixRequestToInsert = createFakeChavePixRequestToInsert(tipoChave = TipoChave.UNKNOWN_KEY_TYPE)

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(chavePixRequestToInsert)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados de entrada invalidos", this.status.description)
        }
    }

    @Test
    fun `nao deve adicionar nova chave pix com chave vazia`() {
        val chavePixRequestToInsert = createFakeChavePixRequestToInsert(chave = "")

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(chavePixRequestToInsert)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados de entrada invalidos", this.status.description)
        }
    }

    @Test
    fun `nao deve adicionar nova chave pix sem o id do cliente`() {
        val chavePixRequestToInsert = createFakeChavePixRequestToInsert(idCliente = "")

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(chavePixRequestToInsert)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados de entrada invalidos", this.status.description)
        }
    }

    @Test
    fun `nao deve aceitar envio de chave quando tipo for aleatoria`() {
        val mockChavePixRequestToInsert = createFakeChavePixRequestToInsert(tipoChave = TipoChave.CHAVE_ALEATORIA)

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(mockChavePixRequestToInsert)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados de entrada invalidos", this.status.description)
        }
    }

    @Test
    fun `deve gerar uma chave randomica quando tipo for aleatoria`() {

        // Cenário
        val chavePixToSave =
            createFakeChavePixRequestToInsert(tipoChave = TipoChave.CHAVE_ALEATORIA, chave = "")

        val fakeBodyResponseErpItauClient = createFakeResponseForErpItauClient()
        `when`(erpItauClient.consultaClientePeloId(chavePixToSave.idCliente, chavePixToSave.tipoConta))
            .thenReturn(HttpResponse.ok(fakeBodyResponseErpItauClient))


        val bacenRequest = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient)
        val fakeBodyResponseBacenClient =
            createFakeResponseForBacenClient(chavePixToSave, fakeBodyResponseErpItauClient)

        `when`(bacenClient.registra(bacenRequest))
            .thenReturn(HttpResponse.created(fakeBodyResponseBacenClient))

        //Ação
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        // Validação
        val registeredPix = repository.findById(UUID.fromString(chavePixResponse.pixId)).get()
        with(registeredPix) {
            assertTrue(
                id.toString().matches("^[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}$".toRegex())
            )
        }

    }

    @Test
    fun `nao deve registrar nova chave quando o tipo da chave for email e email for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                createFakeChavePixRequestToInsert(
                    tipoChave = TipoChave.EMAIL,
                    chave = "emailnaoexiste"
                )
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve registrar nova chave quando tipo da chave for cpf e o numero for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(createFakeChavePixRequestToInsert(tipoChave = TipoChave.CPF, chave = "0158302299"))
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve registrar nova chave quando tipo da chave for telefone celular e o numero for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                createFakeChavePixRequestToInsert(
                    tipoChave = TipoChave.TELEFONE_CELULAR,
                    chave = "36322222"
                )
            )
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve registrar uma chave em caso de erro na comunicacao com a api do bacen`() {
        // Cenário
        val chavePixToSave = createFakeChavePixRequestToInsert(tipoChave = TipoChave.EMAIL, chave = "emailbala@gmail.com")

        val fakeBodyResponseErpItauClient = createFakeResponseForErpItauClient()

        `when`(erpItauClient.consultaClientePeloId(chavePixToSave.idCliente, chavePixToSave.tipoConta))
            .thenReturn(HttpResponse.ok(fakeBodyResponseErpItauClient))

        val bacenRequest = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient)
        `when`(bacenClient.registra(bacenRequest))
            .thenReturn(HttpResponse.badRequest())

        // Ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(chavePixToSave)
        }

        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Problema ao gravar chave no bacen", status.description)
        }

    }


    private fun createFakeChavePixEntityToInsert(contaAssociada: ContaAssociada, owner: Owner): ChavePix {
        return ChavePix(
            idCliente = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoChave.CPF.toString(),
            tipoConta = TipoConta.CONTA_CORRENTE.toString(),
            chave = "82564475590",
            contaAssociada = contaAssociada,
            owner = owner
        )
    }

    private fun createFakeChavePixRequestToInsert(
        chave: String = "82564475590",
        tipoChave: TipoChave = TipoChave.CPF,
        tipoConta: TipoConta = TipoConta.CONTA_CORRENTE,
        idCliente: String = "c56dfef4-7901-44fb-84e2-a2cefb157890"
    ): ChavePixRequest {
        return ChavePixRequest.newBuilder()
            .setChave(chave)
            .setTipoChave(tipoChave)
            .setTipoConta(tipoConta)
            .setIdCliente(idCliente)
            .build()
    }


    private fun createFakeResponseForBacenClient(
        chavePixToSave: ChavePixRequest,
        fakeBodyResponseErpItauClient: DetalhesContaResponse
    ) = ChavePixToRegisterBacenResponse(
        keyType = chavePixToSave.tipoChave,
        key = chavePixToSave.chave,
        bankAccount = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient).bankAccount,
        owner = chavePixToSave.toDto().toBacenModel(fakeBodyResponseErpItauClient).owner,
        createdAt = LocalDateTime.now()
    )

    private fun createFakeResponseForErpItauClient() = DetalhesContaResponse(
        agencia = "001",
        numero = "51511155",
        TipoConta.CONTA_CORRENTE,
        titular = Titular(
            id = UUID.randomUUID().toString(),
            nome = "John Snow",
            cpf = "43994886201"
        )
    )

    @MockBean(ErpItauClient::class)
    fun mockErpItauClient(): ErpItauClient {
        return mock(ErpItauClient::class.java)
    }

    @MockBean(BacenClient::class)
    fun mockBacenClient(): BacenClient {
        return mock(BacenClient::class.java)
    }

    @Factory
    private class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegisterGrpcServiceGrpc.KeyManagerRegisterGrpcServiceBlockingStub {
            return KeyManagerRegisterGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}