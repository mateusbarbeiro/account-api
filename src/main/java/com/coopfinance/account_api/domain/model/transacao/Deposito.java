package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;

import java.math.BigDecimal;

public class Deposito extends Transacao {
    public Deposito(ContaCorrente contaCorrente, BigDecimal valorMovimentado, BigDecimal saldoAnterior, BigDecimal saldoApos) {
        super(contaCorrente, valorMovimentado, saldoAnterior, saldoApos);
    }

    @Override
    public TipoMovimentacao tipo() {
        return TipoMovimentacao.DEPOSITO;
    }
}
