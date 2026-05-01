package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.Documento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SaqueTest {

    @Test
    void deveCriarSaqueComValoresCorretos() {
        ContaCorrente contaCorrente = new ContaCorrente("123456", new Documento("12345678909"));
        BigDecimal valorMovimentado = new BigDecimal("50.00");
        BigDecimal saldoAnterior = new BigDecimal("100.00");
        BigDecimal saldoApos = new BigDecimal("50.00");

        Saque saque = new Saque(contaCorrente, valorMovimentado, saldoAnterior, saldoApos);

        assertNotNull(saque.getId());
        assertEquals(contaCorrente, saque.getContaCorrente());
        assertEquals(valorMovimentado, saque.getValorMovimentado());
        assertNotNull(saque.getDataHoraTransacao());
        assertEquals(saldoAnterior, saque.getSaldoAnterior());
        assertEquals(saldoApos, saque.getSaldoApos());
        assertEquals(Transacao.TipoMovimentacao.SAQUE, saque.tipo());
    }
}
