package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumeroContaTest {

    @Test
    @DisplayName("Deve instanciar NumeroConta com sucesso quando a conta base for válida")
    void deveInstanciarNumeroContaComSucesso() {
        NumeroConta numeroConta = new NumeroConta(123456L);
        assertEquals(123456L, numeroConta.contaBase());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta base for nula")
    void deveLancarExcecaoQuandoContaBaseNula() {
        NumeroContaInvalidoException exception = assertThrows(
                NumeroContaInvalidoException.class,
                () -> new NumeroConta(null)
        );
        assertEquals("A conta base não pode ser nula ou menor ou igual a zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta base for vazia ou conter apenas espaços em branco")
    void deveLancarExcecaoQuandoContaBaseVazia() {
        NumeroContaInvalidoException exception = assertThrows(
                NumeroContaInvalidoException.class,
                () -> new NumeroConta(0L)
        );
        assertEquals("A conta base não pode ser nula ou menor ou igual a zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta base for menor que zero")
    void deveLancarExcecaoQuandoContaBaseMenorQueZero() {
        NumeroContaInvalidoException exception = assertThrows(
                NumeroContaInvalidoException.class,
                () -> new NumeroConta(-1L)
        );
        assertEquals("A conta base não pode ser nula ou menor ou igual a zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular corretamente o dígito verificador usando algoritmo de Luhn (Módulo 10)")
    void deveCalcularDigitoVerificadorComSucesso() {
        // Exemplo 1: Conta base "12345"
        // Invertido: 5 4 3 2 1
        // Multiplicando (pesos 2, 1, 2, 1, 2): 10 4 6 2 2
        // Ajustando (> 9 subtrai 9): 1 4 6 2 2
        // Soma = 15 -> Módulo 10 = 5
        // DV = (10 - 5) % 10 = 5
        NumeroConta numeroConta1 = new NumeroConta(12345L);
        assertEquals(5, numeroConta1.calcularDigitoVerificador(), "Falha no cálculo do DV para a conta 12345");

        // Exemplo 2: Conta base "7992739871"
        // Invertido: 1 7 8 9 3 7 2 9 9 7
        // Multiplicando (pesos 2, 1, 2, 1...): 2 7 16 9 6 7 4 9 18 7
        // Ajustando: 2 7 7 9 6 7 4 9 9 7
        // Soma = 67 -> Módulo 10 = 7
        // DV = (10 - 7) % 10 = 3
        NumeroConta numeroConta2 = new NumeroConta(7992739871L);
        assertEquals(3, numeroConta2.calcularDigitoVerificador(), "Falha no cálculo do DV para a conta 7992739871");

        // Exemplo 4: Conta base "9"
        // Invertido: 9 -> Peso 2 -> 18 -> Ajuste: 9
        // Soma = 9 -> Módulo 10 = 9
        // DV = (10 - 9) % 10 = 1
        NumeroConta numeroConta4 = new NumeroConta(9L);
        assertEquals(1, numeroConta4.calcularDigitoVerificador(), "Falha no cálculo do DV para a conta 9");
    }
}