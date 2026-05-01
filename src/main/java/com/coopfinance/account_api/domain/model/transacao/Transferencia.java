package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class Transferencia extends Transacao {
    private final OrdemTransferencia ordemTransferencia;

    public Transferencia(UUID id, ContaCorrente contaCorrente, BigDecimal valorMovimentado, BigDecimal saldoAnterior, BigDecimal saldoApos, OrdemTransferencia ordemTransferencia) {
        super(id, contaCorrente, valorMovimentado, saldoAnterior, saldoApos);
        this.ordemTransferencia = ordemTransferencia;
    }

    public OrdemTransferencia getOrdemTransferencia() {
        return ordemTransferencia;
    }
}