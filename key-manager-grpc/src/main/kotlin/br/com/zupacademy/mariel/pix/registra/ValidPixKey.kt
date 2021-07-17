package br.com.zupacademy.mariel.pix.registra

import br.com.zupacademy.mariel.TipoChave
import io.micronaut.validation.validator.constraints.EmailValidator
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "Chave Pix invalida ($/{value.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePixDto> {

    override fun isValid(
        value: NovaChavePixDto,
        context: ConstraintValidatorContext?
    ): Boolean {

        return when (value.tipoChave) {
            TipoChave.CHAVE_ALEATORIA -> {
                value.chave.isBlank()
            }
            TipoChave.CPF -> {
                value.chave.matches("^[0-9]{11}$".toRegex())
            }
            TipoChave.TELEFONE_CELULAR -> {
                value.chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
            }
            TipoChave.EMAIL -> {
                EmailValidator().run {
                    initialize(null)
                    isValid(value.chave, null)
                }
            }
            else -> false
        }
    }

}