package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.ChavePixRequest
import br.com.zupacademy.mariel.KeyManagerGrpcServiceGrpc
import br.com.zupacademy.mariel.TipoChave
import br.com.zupacademy.mariel.TipoConta
import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton


@MicronautTest(transactional = false)
internal class CadastroNovaChavePixEndPointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub,
) {

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave pix a partir de um cpf valido`() {

        val chavePixToSave = MockChavePixRequestToInsert(tipoChave = TipoChave.CPF, chave = "82564475590" )
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        with(chavePixResponse) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsByChave(this.chave))
        }

        val chavePixInDatabase = repository.findByChave(chavePixResponse.chave)
        with(chavePixInDatabase) {
            assertEquals(chavePixToSave.tipoChave.toString(), tipoChave)
            assertEquals(chavePixToSave.tipoConta.toString(), tipoConta)
            assertEquals(chavePixToSave.idCliente, idCliente)
            assertNotNull(id)
        }

    }

    @Test
    fun `deve registrar uma nova chave pix a partir de um celular valido`() {

        val chavePixToSave = MockChavePixRequestToInsert(tipoChave = TipoChave.TELEFONE_CELULAR,
            chave = "+5585988714077" )
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        with(chavePixResponse) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsByChave(this.chave))
        }

        val chavePixInDatabase = repository.findByChave(chavePixResponse.chave)
        with(chavePixInDatabase) {
            assertEquals(chavePixToSave.tipoChave.toString(), tipoChave)
            assertEquals(chavePixToSave.tipoConta.toString(), tipoConta)
            assertEquals(chavePixToSave.idCliente, idCliente)
            assertNotNull(id)
        }

    }

    @Test
    fun `deve registrar uma nova chave pix a partir de um email valido`() {

        val chavePixToSave = MockChavePixRequestToInsert(tipoChave = TipoChave.EMAIL,
            chave = "emailquentucho@gmail.com" )
        val chavePixResponse = grpcClient.cadastrar(chavePixToSave)

        with(chavePixResponse) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsByChave(this.chave))
        }

        val chavePixInDatabase = repository.findByChave(chavePixResponse.chave)
        with(chavePixInDatabase) {
            assertEquals(chavePixToSave.tipoChave.toString(), tipoChave)
            assertEquals(chavePixToSave.tipoConta.toString(), tipoConta)
            assertEquals(chavePixToSave.idCliente, idCliente)
            assertNotNull(id)
        }

    }

    @Test
    fun `nao deve adicionar uma chave já existente`() {
        val existente = repository.save(MockChavePixEntityToInsert())

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(MockChavePixRequestToInsert())

        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Chave Pix ${existente.chave} existente", this.status.description)
        }

    }

    @Test
    fun `nao deve adicionar nova chave pix com tipo de conta invalido`() {
        val chavePixRequestToInsert = MockChavePixRequestToInsert(tipoConta = TipoConta.UNKNOWN_ACCOUNT)

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
        val chavePixRequestToInsert = MockChavePixRequestToInsert(tipoChave = TipoChave.UNKNOWN_KEY_TYPE)

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
        val chavePixRequestToInsert = MockChavePixRequestToInsert(chave = "")

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
        val chavePixRequestToInsert = MockChavePixRequestToInsert(idCliente = "")

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
        val mockChavePixRequestToInsert = MockChavePixRequestToInsert(tipoChave = TipoChave.CHAVE_ALEATORIA)

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
        val mockChavePixRequestToInsert = MockChavePixRequestToInsert(tipoChave = TipoChave.CHAVE_ALEATORIA, chave = "")
        val chavePixResponse = grpcClient.cadastrar(mockChavePixRequestToInsert).chave

        val registeredPix = repository.findByChave(chavePixResponse)

        with(registeredPix) {
            assertTrue(chave.matches("^[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}$".toRegex()))
        }

    }

    @Test
    fun `nao deve registrar nova chave quando o tipo da chave for email e email for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(MockChavePixRequestToInsert(tipoChave = TipoChave.EMAIL, chave = "emailnaoexiste"))
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve registrar nova chave quando tipo da chave for cpf e o numero for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(MockChavePixRequestToInsert(tipoChave = TipoChave.CPF, chave = "0158302299"))
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve registrar nova chave quando tipo da chave for telefone celular e o numero for invalido`() {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(MockChavePixRequestToInsert(tipoChave = TipoChave.TELEFONE_CELULAR, chave = "36322222"))
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    private fun MockChavePixEntityToInsert(): ChavePix {
        return ChavePix(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChave.CPF.toString(),
            TipoConta.CORRENTE.toString(),
            "82564475590"
        )
    }

    private fun MockChavePixRequestToInsert(
        chave: String = "82564475590",
        tipoChave: TipoChave = TipoChave.CPF,
        tipoConta: TipoConta = TipoConta.CORRENTE,
        idCliente: String = "c56dfef4-7901-44fb-84e2-a2cefb157890"
    ): ChavePixRequest {
        return ChavePixRequest.newBuilder()
            .setChave(chave)
            .setTipoChave(tipoChave)
            .setTipoConta(tipoConta)
            .setIdCliente(idCliente)
            .build()
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub {
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}