package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.pix.integracao.bacen.BacenClient
import br.com.zupacademy.mariel.pix.integracao.erp.ErpItauClient
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastroNovaChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val bacenClient: BacenClient,
    @Inject val erpItauClient: ErpItauClient
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePixDto): ChavePix? {

        if (repository.existsByChave(novaChavePix.chave)) {
            throw ChavePixExistenteException("Chave Pix ${novaChavePix.chave} existente")
        }

        // Buscar uma conta associada
        val request = erpItauClient.consultaClientePeloId(novaChavePix.idCliente, novaChavePix.tipoConta)
        if (request.status.code != 200){
            throw IllegalStateException("Problema ao buscar uma conta deste tipo com este ID")
        }

        val detalhesDaConta = request.body()
        val bacenRequestDto = novaChavePix.toBacenModel(detalhesDaConta)

        // Gravar no bacen
        val bacenRequest = bacenClient.registra(bacenRequestDto)
        if (bacenRequest.status.code != 201){
            LOGGER.info(bacenRequest.status.name)
            throw IllegalStateException("Problema ao gravar chave no bacen")
        }

        val registeredPix = repository.save(novaChavePix.toEntity(detalhesContaResponse = detalhesDaConta))

        return registeredPix
    }
}