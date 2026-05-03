package com.coopfinance.account_api.domain.model.transacao;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class Transacao {
    public enum TipoMovimentacao {
        SAQUE, DEPOSITO, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECEBIDA
    }

    private final UUID id;
    private final ContaCorrente contaCorrente;
    private final BigDecimal valorMovimentado;
    private final LocalDateTime dataHoraTransacao;
    private final BigDecimal saldoAnterior;
    private final BigDecimal saldoApos;

    public Transacao(UUID id, ContaCorrente contaCorrente, BigDecimal valorMovimentado, BigDecimal saldoAnterior, BigDecimal saldoApos) {
        this.id = id;
        this.contaCorrente = contaCorrente;
        this.valorMovimentado = valorMovimentado;
        this.dataHoraTransacao = LocalDateTime.now();
        this.saldoAnterior = saldoAnterior;
        this.saldoApos = saldoApos;
    }

    public abstract TipoMovimentacao tipo();

}
