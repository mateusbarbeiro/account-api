package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepositoTest {

    @Test
    void deveRetornarTipoMovimentacaoCorreto() {
        final ContaCorrente conta = new ContaCorrente(UUID.randomUUID(), 123456L, "12345678909");
        final Deposito deposito = new Deposito(
                UUID.randomUUID(),
                conta,
                BigDecimal.TEN,
                BigDecimal.ZERO,
                BigDecimal.TEN
        );

        assertNotNull(deposito.getId());
        assertEquals(conta, deposito.getContaCorrente());
        assertEquals(BigDecimal.TEN, deposito.getValorMovimentado());
        assertNotNull(deposito.getDataHoraTransacao());
        assertEquals(BigDecimal.ZERO, deposito.getSaldoAnterior());
        assertEquals(BigDecimal.TEN, deposito.getSaldoApos());
        assertEquals(Transacao.TipoMovimentacao.DEPOSITO, deposito.tipo());
    }
}
