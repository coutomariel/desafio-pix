package br.com.zupacademy.mariel.pix.integracao.erp

import br.com.zupacademy.mariel.TipoConta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9091/api/v1/")
interface ErpItauClient {

    @Get("clientes/{idCliente}/contas?tipo={tipo}")
    fun consultaClientePeloId(idCliente: String, tipo: TipoConta?) : HttpResponse<DetalhesContaResponse>
}