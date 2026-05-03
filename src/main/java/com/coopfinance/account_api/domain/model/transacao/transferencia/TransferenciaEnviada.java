package com.coopfinance.account_api.domain.model.transacao.transferencia;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferenciaEnviada extends Transferencia {
    public TransferenciaEnviada(UUID id, ContaCorrente contaCorrente, BigDecimal valorMovimentado, BigDecimal saldoAnterior, BigDecimal saldoApos, OrdemTransferencia ordemTransferencia) {
        super(id, contaCorrente, valorMovimentado, saldoAnterior, saldoApos, ordemTransferencia);
    }

    @Override
    public TipoMovimentacao tipo() {
        return TipoMovimentacao.TRANSFERENCIA_ENVIADA;
    }
}
