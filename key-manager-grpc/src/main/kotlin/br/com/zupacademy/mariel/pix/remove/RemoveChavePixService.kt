package br.com.zupacademy.mariel.pix.remove

import br.com.zupacademy.mariel.domain.ChavePixRepository
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository) {

    @Transactional
    fun remover(@Valid pixToRemovetoDto: ChavePixToRemoveDto) {

        val pixExisteNaBase = repository.existsByIdAndIdCliente(UUID.fromString(pixToRemovetoDto.pixId), pixToRemovetoDto.idCliente)
        if(!pixExisteNaBase) {
            throw ChavePixNaoEncontradaException("Chave pix nao encotrada para o ID ${pixToRemovetoDto.pixId}")
        }

        repository.deleteById(UUID.fromString(pixToRemovetoDto.pixId))
    }

}
