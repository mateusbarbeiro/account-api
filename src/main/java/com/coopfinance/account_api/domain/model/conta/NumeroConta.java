package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;

public record NumeroConta(String contaBase) {

    public static final String NUMERIC_REGEX = "\\d+";

    public NumeroConta {
        validarConsistencia(contaBase);
    }

    /**
     * Calcula o Dígito Verificador (Módulo 10 - Luhn) para a conta base.
     * * A fórmula (10 - (soma % 10)) % 10 garante que o dígito complete a dezena.
     * O último '% 10' assegura que, se o resto for 0, o dígito será 0 (e não 10).
     *
     * @return O dígito verificador calculado.
     */
    public int calcularDigitoVerificador() {
        return (10 - (calculaSomaContaBase(contaBase) % 10)) % 10;
    }

    public String contaComposta() {
        return contaBase + "-" + calcularDigitoVerificador();
    }

    private void validarConsistencia(String contaBase) {
        validarVazioOuNulo(contaBase);
        validarFormatoValido(contaBase);
    }

    private static void validarFormatoValido(String contaBase) {
        if (!contaBase.matches(NUMERIC_REGEX))
            throw new NumeroContaInvalidoException("A conta base deve conter apenas números.");
    }

    private static void validarVazioOuNulo(String contaBase) {
        if (contaBase == null || contaBase.isBlank())
            throw new NumeroContaInvalidoException("A conta base não pode ser nula ou vazia.");
    }

    /**
     * Calcula a soma ponderada dos dígitos da conta base, aplicando as regras do algoritmo de Luhn.
     * O processamento ocorre da direita para a esquerda, alternando os pesos (multiplicadores) para cada dígito.
     *
     * @param contaBase O número da conta base a ser processado.
     * @return O valor total da soma calculada.
     */
    private static int calculaSomaContaBase(String contaBase) {
        // Começamos com 'true' porque o último dígito da base vai multiplicar por 2. Para cada item iterado, altera estado.
        boolean multiplicarPorDois = true;
        int soma = 0;

        for (char caractere : new StringBuilder(contaBase).reverse().toString().toCharArray()) {
            int digito = Character.getNumericValue(caractere);
            soma += aplicaRegraLuhn(multiplicarPorDois, digito);
            multiplicarPorDois = !multiplicarPorDois;
        }

        return soma;
    }

    /**
     * Aplica a regra de multiplicação do algoritmo de Luhn a um dígito específico,
     * caso a flag de alternância indique que ele deve receber o peso.
     *
     * @param isMultiplicarPorDois Flag indicando se o peso atual do ciclo é 2.
     * @param digito O dígito atual sendo processado.
     * @return O valor final do dígito após aplicar (ou não) a regra de peso.
     */
    private static int aplicaRegraLuhn(boolean isMultiplicarPorDois, int digito) {
        if (isMultiplicarPorDois)
            digito = aplicaMultiplicacaoParaLuhn(digito);

        return digito;
    }

    /**
     * Aplica o peso dobrado ao dígito. Pela regra de Luhn, se o resultado da
     * multiplicação passar de 9, somamos os dois algarismos do resultado
     * (o que equivale matematicamente a subtrair 9).
     * Exemplo: 7 * 2 = 14 -> 1 + 4 = 5 (ou 14 - 9 = 5).
     *
     * @param digito O dígito que será multiplicado.
     * @return O dígito resultante ajustado pela regra de Luhn.
     */
    private static int aplicaMultiplicacaoParaLuhn(int digito) {
        digito *= 2;
        if (digito > 9)
            digito -= 9;

        return digito;
    }

}