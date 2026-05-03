package com.coopfinance.account_api.domain.model.transacao.transferencia;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;
import com.coopfinance.account_api.domain.model.transacao.Transacao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransferenciaRecebidaTest {

    @Test
    void deveCriarTransferenciaRecebidaComValoresCorretos() {
        ContaCorrente contaOrigem = new ContaCorrente(UUID.randomUUID(), 123456L, "12345678909");
        ContaCorrente contaDestino = new ContaCorrente(UUID.randomUUID(), 654321L, "98765432100");
        
        BigDecimal valorMovimentado = new BigDecimal("50.00");
        BigDecimal saldoAnterior = new BigDecimal("100.00");
        BigDecimal saldoApos = new BigDecimal("150.00");
        
        OrdemTransferencia ordemTransferencia = new OrdemTransferencia(UUID.randomUUID(), contaOrigem, contaDestino, new BigDecimal("50.00"));

        TransferenciaRecebida transferenciaRecebida = new TransferenciaRecebida(
                UUID.randomUUID(),
                contaDestino, 
                valorMovimentado, 
                saldoAnterior, 
                saldoApos, 
                ordemTransferencia
        );

        assertNotNull(transferenciaRecebida.getId());
        assertEquals(contaDestino, transferenciaRecebida.getContaCorrente());
        assertEquals(valorMovimentado, transferenciaRecebida.getValorMovimentado());
        assertNotNull(transferenciaRecebida.getDataHoraTransacao());
        assertEquals(saldoAnterior, transferenciaRecebida.getSaldoAnterior());
        assertEquals(saldoApos, transferenciaRecebida.getSaldoApos());
        Assertions.assertEquals(Transacao.TipoMovimentacao.TRANSFERENCIA_RECEBIDA, transferenciaRecebida.tipo());
        assertEquals(ordemTransferencia, transferenciaRecebida.getOrdemTransferencia());
    }
}
