package com.coopfinance.account_api.domain.model.operacao;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.Documento;
import com.coopfinance.account_api.domain.model.transacao.Transferencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrdemTransferenciaTest {

    private ContaCorrente contaOrigem;
    private ContaCorrente contaDestino;

    @BeforeEach
    void setUp() {
        Documento documento1 = new Documento("12345678901");
        Documento documento2 = new Documento("98765432109");
        contaOrigem = new ContaCorrente("11111", documento1);
        contaDestino = new ContaCorrente("22222", documento2);
        contaOrigem.depositar(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Deve criar uma ordem de transferência com sucesso")
    void deveCriarOrdemTransferenciaComSucesso() {
        BigDecimal valor = new BigDecimal("100.00");
        OrdemTransferencia ordem = new OrdemTransferencia(contaOrigem, contaDestino, valor);

        assertNotNull(ordem.getId());
        assertEquals(contaOrigem, ordem.getContaOrigem());
        assertEquals(contaDestino, ordem.getContaDestino());
        assertEquals(valor, ordem.getValor());
        assertEquals(OrdemTransferencia.StatusTransferencia.PENDENTE, ordem.getStatus());
        assertNotNull(ordem.getDataHoraSolicitacao());
        assertTrue(ordem.getTransacoesGeradas().isEmpty());
    }

    @Test
    @DisplayName("Não deve criar ordem de transferência com valor zero")
    void naoDeveCriarOrdemComValorZero() {
        ValorInvalidoException exception = assertThrows(ValorInvalidoException.class, () ->
            new OrdemTransferencia(contaOrigem, contaDestino, BigDecimal.ZERO)
        );
        assertEquals("Valor da transferência deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve criar ordem de transferência com valor negativo")
    void naoDeveCriarOrdemComValorNegativo() {
        ValorInvalidoException exception = assertThrows(ValorInvalidoException.class, () ->
            new OrdemTransferencia(contaOrigem, contaDestino, new BigDecimal("-50.00"))
        );
        assertEquals("Valor da transferência deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve criar ordem de transferência com valor nulo")
    void naoDeveCriarOrdemComValorNulo() {
        ValorInvalidoException exception = assertThrows(ValorInvalidoException.class, () ->
            new OrdemTransferencia(contaOrigem, contaDestino, null)
        );
        assertEquals("Valor da transferência deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve criar ordem de transferência para a mesma conta")
    void naoDeveCriarOrdemParaMesmaConta() {
        NumeroContaInvalidoException exception = assertThrows(NumeroContaInvalidoException.class, () ->
            new OrdemTransferencia(contaOrigem, contaOrigem, new BigDecimal("100.00"))
        );
        assertEquals("A conta de origem e destino não podem ser as mesmas.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve efetivar a transferência com sucesso")
    void deveEfetivarTransferenciaComSucesso() {
        BigDecimal valor = new BigDecimal("200.00");
        OrdemTransferencia ordem = new OrdemTransferencia(contaOrigem, contaDestino, valor);

        ordem.efetivar();

        assertEquals(OrdemTransferencia.StatusTransferencia.CONCLUIDA, ordem.getStatus());
        assertEquals(new BigDecimal("800.00"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("200.00"), contaDestino.getSaldo());
        assertEquals(2, ordem.getTransacoesGeradas().size());
    }

    @Test
    @DisplayName("Deve falhar ao tentar efetivar transferência com saldo insuficiente")
    void deveFalharAoEfetivarComSaldoInsuficiente() {
        BigDecimal valor = new BigDecimal("1500.00"); // Maior que o saldo da contaOrigem
        OrdemTransferencia ordem = new OrdemTransferencia(contaOrigem, contaDestino, valor);

        assertThrows(SaldoInsuficienteException.class, ordem::efetivar);

        assertEquals(OrdemTransferencia.StatusTransferencia.FALHOU, ordem.getStatus());
        assertEquals(new BigDecimal("1000.00"), contaOrigem.getSaldo()); // Saldo não deve mudar
        assertEquals(BigDecimal.ZERO, contaDestino.getSaldo()); // Saldo não deve mudar
        assertTrue(ordem.getTransacoesGeradas().isEmpty()); // Nenhuma transação deve ser gerada
    }

    @Test
    @DisplayName("Deve gerar transações de débito e crédito corretamente")
    void deveGerarTransacoesCorretamente() {
        BigDecimal valor = new BigDecimal("150.00");
        OrdemTransferencia ordem = new OrdemTransferencia(contaOrigem, contaDestino, valor);

        ordem.efetivar();

        Transferencia transacaoDebito = ordem.getTransacoesGeradas().stream()
                .filter(t -> t.getContaCorrente().equals(contaOrigem))
                .findFirst().orElse(null);

        Transferencia transacaoCredito = ordem.getTransacoesGeradas().stream()
                .filter(t -> t.getContaCorrente().equals(contaDestino))
                .findFirst().orElse(null);

        assertNotNull(transacaoDebito);
        assertEquals(ordem, transacaoDebito.getOrdemTransferencia());
        assertEquals(valor.negate(), transacaoDebito.getValorMovimentado());
        assertEquals(new BigDecimal("1000.00"), transacaoDebito.getSaldoAnterior());
        assertEquals(new BigDecimal("850.00"), transacaoDebito.getSaldoApos());

        assertNotNull(transacaoCredito);
        assertEquals(ordem, transacaoCredito.getOrdemTransferencia());
        assertEquals(valor, transacaoCredito.getValorMovimentado());
        assertEquals(BigDecimal.ZERO, transacaoCredito.getSaldoAnterior());
        assertEquals(new BigDecimal("150.00"), transacaoCredito.getSaldoApos());
    }
}