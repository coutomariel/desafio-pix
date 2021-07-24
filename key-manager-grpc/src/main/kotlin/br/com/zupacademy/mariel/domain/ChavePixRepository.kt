package br.com.zupacademy.mariel.domain

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {
    fun existsByChave(chave: String): Boolean
    fun findByIdAndIdCliente(id : UUID, idCliente: String): Optional<ChavePix>
    fun findByChave(chave: String) : Optional<ChavePix>
    fun findByIdCliente(clientId: String): List<ChavePix>
    fun existsByIdCliente(clientId: String): Boolean
}