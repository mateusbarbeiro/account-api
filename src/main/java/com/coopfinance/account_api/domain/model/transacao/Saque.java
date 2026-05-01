package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;

import java.math.BigDecimal;
import java.util.UUID;

public class Saque extends Transacao {
    public Saque(UUID id, ContaCorrente contaCorrente, BigDecimal valorMovimentado, BigDecimal saldoAnterior, BigDecimal saldoApos) {
        super(id, contaCorrente, valorMovimentado, saldoAnterior, saldoApos);
    }

    @Override
    public TipoMovimentacao tipo() {
        return TipoMovimentacao.SAQUE;
    }
}
