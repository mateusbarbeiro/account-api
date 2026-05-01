package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContaCorrenteTest {

    @Test
    @DisplayName("Deve criar conta corrente com sucesso usando construtor simplificado")
    void deveCriarContaCorrenteConstrutorSimplificado() {
        Documento documento = new Documento("12345678909");
        ContaCorrente conta = new ContaCorrente(UUID.randomUUID(), "12345", documento);

        assertNotNull(conta.getId());
        assertEquals("12345", conta.getNumeroConta());
        assertEquals("5", conta.getDigitoVerificadorConta());
        assertEquals("12345-5", conta.getNumeroContaComposto());
        assertEquals(documento, conta.getDocumento());
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
        assertEquals(0L, conta.getVersao());
    }

    @Test
    @DisplayName("Deve criar conta corrente com sucesso usando construtor completo")
    void deveCriarContaCorrenteConstrutorCompleto() {
        UUID id = UUID.randomUUID();
        Documento documento = new Documento("12345678909");
        BigDecimal saldoInicial = new BigDecimal("100.00");
        ContaCorrente conta = new ContaCorrente(id, "12345", "5", documento, saldoInicial, 1L);

        assertEquals(id, conta.getId());
        assertEquals("12345", conta.getNumeroConta());
        assertEquals("5", conta.getDigitoVerificadorConta());
        assertEquals("12345-5", conta.getNumeroContaComposto());
        assertEquals(documento, conta.getDocumento());
        assertEquals(saldoInicial, conta.getSaldo());
        assertEquals(1L, conta.getVersao());
    }

    @Test
    @DisplayName("Não deve criar conta corrente com dígito verificador inválido")
    void naoDeveCriarContaComDigitoInvalido() {
        UUID id = UUID.randomUUID();
        Documento documento = new Documento("12345678909");
        
        NumeroContaInvalidoException exception = assertThrows(NumeroContaInvalidoException.class, () ->
            new ContaCorrente(id, "12345", "9", documento, BigDecimal.ZERO, 0L)
        );
        
        assertEquals("Dígito verificador inválido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve realizar saque com sucesso quando houver saldo suficiente")
    void deveRealizarSaqueComSucesso() {
        UUID id = UUID.randomUUID();
        Documento documento = new Documento("12345678909");
        ContaCorrente conta = new ContaCorrente(id, "12345", "5", documento, new BigDecimal("100.00"), 0L);

        conta.sacar(new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("Não deve permitir saque quando o saldo for insuficiente")
    void naoDevePermitirSaqueComSaldoInsuficiente() {
        UUID id = UUID.randomUUID();
        Documento documento = new Documento("12345678909");
        ContaCorrente conta = new ContaCorrente(id, "12345", "5", documento, new BigDecimal("50.00"), 0L);

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () ->
            conta.sacar(new BigDecimal("60.00"))
        );

        assertEquals("Saldo insuficiente.", exception.getMessage());
        assertEquals(new BigDecimal("50.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("Não deve permitir saque com valor negativo ou nulo")
    void naoDevePermitirSaqueComValorInvalido() {
        Documento documento = new Documento("12345678909");
        ContaCorrente conta = new ContaCorrente(UUID.randomUUID(), "12345", documento);
        conta.depositar(new BigDecimal("100.00"));

        assertThrows(ValorInvalidoException.class, () -> conta.sacar(BigDecimal.ZERO));
        assertThrows(ValorInvalidoException.class, () -> conta.sacar(new BigDecimal("-10.00")));
        assertThrows(ValorInvalidoException.class, () -> conta.sacar(null));
    }

    @Test
    @DisplayName("Deve realizar depósito com sucesso")
    void deveRealizarDepositoComSucesso() {
        Documento documento = new Documento("12345678909");
        ContaCorrente conta = new ContaCorrente(UUID.randomUUID(), "12345", documento);

        conta.depositar(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("50.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("Não deve permitir depósito com valor negativo ou nulo")
    void naoDevePermitirDepositoComValorInvalido() {
        Documento documento = new Documento("12345678909");
        ContaCorrente conta = new ContaCorrente(UUID.randomUUID(), "12345", documento);

        assertThrows(ValorInvalidoException.class, () -> conta.depositar(BigDecimal.ZERO));
        assertThrows(ValorInvalidoException.class, () -> conta.depositar(new BigDecimal("-20.00")));
        assertThrows(ValorInvalidoException.class, () -> conta.depositar(null));
    }
}
