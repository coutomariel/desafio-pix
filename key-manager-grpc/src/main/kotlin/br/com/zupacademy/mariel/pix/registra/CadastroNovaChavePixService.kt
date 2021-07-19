package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.domain.ChavePix
import br.com.zupacademy.mariel.domain.ChavePixRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastroNovaChavePixService(@Inject val repository: ChavePixRepository) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePixDto) : ChavePix? {

        if (repository.existsByChave(novaChavePix.chave)) {
            throw ChavePixExistenteException("Chave Pix ${novaChavePix.chave} existente")
        }

        val registeredPix = repository.save(novaChavePix.toEntity())

        return registeredPix
    }
}