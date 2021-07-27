package br.com.zupacademy.mariel.pix.registro

import br.com.zupacademy.mariel.TipoChave
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "Chave Pix invalida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Introspected
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePixRequest>{
    override fun isValid(
        value: NovaChavePixRequest,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        return when (value.tipoChave) {
            TipoChave.CHAVE_ALEATORIA -> {
                value.chave.toString().isBlank()
            }
            TipoChave.CPF -> {
                value.chave!!.matches("^[0-9]{11}$".toRegex())
            }
            TipoChave.TELEFONE_CELULAR -> {
                value.chave!!.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
            }
            TipoChave.EMAIL -> {
                value.chave!!.matches("^([\\w\\.\\-]+)@([\\w\\-]+)((\\.(\\w){2,3})+)\$".toRegex())
//                return EmailValidator().run {
//                    initialize(null)
//                    isValid(value.chave, null)
//                }
            }
            else -> true
        }


    }

}
