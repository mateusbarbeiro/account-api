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

class TransferenciaEnviadaTest {

    @Test
    void deveCriarTransferenciaEnviadaComValoresCorretos() {
        ContaCorrente contaOrigem = new ContaCorrente(UUID.randomUUID(), 123456L, "12345678909");
        ContaCorrente contaDestino = new ContaCorrente(UUID.randomUUID(), 654321L, "98765432100");
        
        BigDecimal valorMovimentado = new BigDecimal("-50.00");
        BigDecimal saldoAnterior = new BigDecimal("100.00");
        BigDecimal saldoApos = new BigDecimal("50.00");
        
        OrdemTransferencia ordemTransferencia = new OrdemTransferencia(UUID.randomUUID(), contaOrigem, contaDestino, new BigDecimal("50.00"));

        TransferenciaEnviada transferenciaEnviada = new TransferenciaEnviada(
                UUID.randomUUID(),
                contaOrigem, 
                valorMovimentado, 
                saldoAnterior, 
                saldoApos, 
                ordemTransferencia
        );

        assertNotNull(transferenciaEnviada.getId());
        assertEquals(contaOrigem, transferenciaEnviada.getContaCorrente());
        assertEquals(valorMovimentado, transferenciaEnviada.getValorMovimentado());
        assertNotNull(transferenciaEnviada.getDataHoraTransacao());
        assertEquals(saldoAnterior, transferenciaEnviada.getSaldoAnterior());
        assertEquals(saldoApos, transferenciaEnviada.getSaldoApos());
        Assertions.assertEquals(Transacao.TipoMovimentacao.TRANSFERENCIA_ENVIADA, transferenciaEnviada.tipo());
        assertEquals(ordemTransferencia, transferenciaEnviada.getOrdemTransferencia());
    }
}
