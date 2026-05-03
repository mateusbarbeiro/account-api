package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;

public record NumeroConta(Long contaBase) {

    public NumeroConta {
        if (contaBase == null || contaBase <= 0)
            throw new NumeroContaInvalidoException("A conta base não pode ser nula ou menor ou igual a zero.");
    }

    /**
     * Calcula o Dígito Verificador (Módulo 10 - Luhn) para a conta base.
     *
     * @return O dígito verificador calculado.
     */
    public int calcularDigitoVerificador() {
        return (10 - (calculaSomaContaBase(String.valueOf(contaBase)) % 10)) % 10;
    }

    public String contaComposta() {
        return contaBase + "-" + calcularDigitoVerificador();
    }

    /**
     * Calcula a soma ponderada dos dígitos da conta base, aplicando as regras do algoritmo de Luhn.
     * O processamento ocorre da direita para a esquerda, alternando os pesos (multiplicadores) para cada dígito.
     *
     * @param contaBase O número da conta base a ser processado (como string para iteração).
     * @return O valor total da soma calculada.
     */
    private static int calculaSomaContaBase(String contaBase) {
        boolean multiplicarPorDois = true;
        int soma = 0;

        for (char caractere : new StringBuilder(contaBase).reverse().toString().toCharArray()) {
            int digito = Character.getNumericValue(caractere);
            soma += aplicaRegraLuhn(multiplicarPorDois, digito);
            multiplicarPorDois = !multiplicarPorDois;
        }

        return soma;
    }

    private static int aplicaRegraLuhn(boolean isMultiplicarPorDois, int digito) {
        if (isMultiplicarPorDois)
            digito = aplicaMultiplicacaoParaLuhn(digito);

        return digito;
    }

    private static int aplicaMultiplicacaoParaLuhn(int digito) {
        digito *= 2;
        if (digito > 9)
            digito -= 9;

        return digito;
    }

}