package br.com.zupacademy.mariel.pix.integracao.bacen

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8082/api/v1/pix/keys")
interface BacenClient {

    @Post
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun registra(@Body chave: ChavePixToRegisterBacenRequest): HttpResponse<ChavePixToRegisterBacenResponse>

    @Delete("/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun remove(@Body toRemove: ChavePixToRemoveBacenRequest, key: String): HttpResponse<ChavePixToRemoveBacenResponse>

    @Get("/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    fun consulta(key : String) : HttpResponse<ChavePixByChaveBacenResponse>
}
