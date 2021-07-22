package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.pix.integracao.bacen.BacenClient
import br.com.zupacademy.mariel.pix.integracao.bacen.BanckAccount
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixToRemoveBacenRequest
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val bacenClient: BacenClient
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun remover(@Valid pixToRemove: ChavePixToRemove) {

        val searchInDatabase = repository.findByIdAndIdCliente(UUID.fromString(pixToRemove.pixId), pixToRemove.idCliente)
        if(searchInDatabase.isEmpty()) {
            throw ChavePixNaoEncontradaException("Chave pix nao encotrada para o ID ${pixToRemove.pixId}")
        }

        val pixInDatabase = searchInDatabase.get()
        val chavePixToRemoveBacenRequest = ChavePixToRemoveBacenRequest(pixInDatabase.chave, BanckAccount.getParticipant())


        val bacenResponse = bacenClient.remove(chavePixToRemoveBacenRequest, pixInDatabase.chave)
        if (bacenResponse.status.code != 200){
            LOGGER.info(bacenResponse.status.name)
            throw IllegalStateException("Problema inesperado na tentativa de exluir a chave")
        }

        repository.deleteById(UUID.fromString(pixToRemove.pixId))
    }

}
