package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.Documento;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransferenciaEnviadaTest {

    @Test
    void deveCriarTransferenciaEnviadaComValoresCorretos() {
        ContaCorrente contaOrigem = new ContaCorrente("123456", new Documento("12345678909"));
        ContaCorrente contaDestino = new ContaCorrente("654321", new Documento("98765432100"));
        
        BigDecimal valorMovimentado = new BigDecimal("-50.00");
        BigDecimal saldoAnterior = new BigDecimal("100.00");
        BigDecimal saldoApos = new BigDecimal("50.00");
        
        OrdemTransferencia ordemTransferencia = new OrdemTransferencia(contaOrigem, contaDestino, new BigDecimal("50.00"));

        TransferenciaEnviada transferenciaEnviada = new TransferenciaEnviada(
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
        assertEquals(Transacao.TipoMovimentacao.TRANSFERENCIA_ENVIADA, transferenciaEnviada.tipo());
        assertEquals(ordemTransferencia, transferenciaEnviada.getOrdemTransferencia());
    }
}
