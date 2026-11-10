package one.digitalinnovation.gof.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Monta a cadeia de validação. Os validadores não são beans: só o primeiro elo
 * é exposto ao Spring, e a ordem da cadeia fica visível em um único lugar.
 */
@Configuration
public class ValidacaoConfig {

	@Bean
	public ValidadorCliente cadeiaDeValidacaoCliente() {
		ValidadorCliente primeiro = new ValidadorNome();
		primeiro.proximo(new ValidadorEndereco()).proximo(new ValidadorCep());
		return primeiro;
	}
}
