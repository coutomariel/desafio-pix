package br.com.zupacademy.mariel.pix.consulta

import br.com.zupacademy.mariel.ChavePixDetailsResponse
import br.com.zupacademy.mariel.ChavePixToSearchRequest
import br.com.zupacademy.mariel.domain.ChavePixRepository
import br.com.zupacademy.mariel.pix.integracao.bacen.BacenClient
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixByChaveBacenResponse
import br.com.zupacademy.mariel.pix.integracao.bacen.ChavePixToRegisterBacenResponse
import br.com.zupacademy.mariel.pix.remove.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Validated
class ConsultaChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val bacenClient: BacenClient,
) {

    fun consultaChavePix(filtro: ChavePixToSearchRequest): ChavePixDetailsResponse? {
        return when (filtro.getSearchType()) {
            TipoPesquisa.POR_CHAVE -> getPixByChave(filtro)
            TipoPesquisa.POR_PIX_ID -> getPixByPixId(filtro)
        }
    }

    fun getPixByChave(filtro: ChavePixToSearchRequest): ChavePixDetailsResponse? {

        if(repository.existsByChave(filtro.chave)){
            return repository.findByChave(filtro.chave).get().toChavePixDetailsResponse()
        }

        val bacenRequest = bacenClient.consulta(filtro.chave)
        if (bacenRequest.status.code != 200) {
            throw ChavePixNaoEncontradaException("Chave pix nao encotrada com os parametros informados")
        }
        val bacenResponse: ChavePixByChaveBacenResponse = bacenRequest.body()
        return bacenResponse.toChavePixDetailsResponse()
    }

    fun getPixByPixId(filtro: ChavePixToSearchRequest) : ChavePixDetailsResponse? {
        val pixByPixId = repository.findByIdAndIdCliente(
            UUID.fromString(filtro.filtroPorPixId.pixId),
            filtro.filtroPorPixId.idCliente
        ).orElseThrow { ChavePixNaoEncontradaException("Chave pix nao encotrada com os parametros informados") }

        return pixByPixId.toChavePixDetailsResponse()
    }

}