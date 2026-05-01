package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;

import java.math.BigDecimal;

public class TransferenciaRecebida extends Transferencia {
    public TransferenciaRecebida(ContaCorrente contaCorrente, BigDecimal valorMovimentado, BigDecimal saldoAnterior, BigDecimal saldoApos, OrdemTransferencia ordemTransferencia) {
        super(contaCorrente, valorMovimentado, saldoAnterior, saldoApos, ordemTransferencia);
    }

    @Override
    public TipoMovimentacao tipo() {
        return TipoMovimentacao.TRANSFERENCIA_RECEBIDA;
    }
}
