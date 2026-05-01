package com.coopfinance.account_api.domain.model.operacao;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.TransferenciaStatusInvalidaException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.transacao.Transferencia;
import com.coopfinance.account_api.domain.model.transacao.TransferenciaEnviada;
import com.coopfinance.account_api.domain.model.transacao.TransferenciaRecebida;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrdemTransferencia {

    public enum StatusTransferencia {
        PENDENTE, CONCLUIDA, FALHOU
    }

    private final UUID id;
    private final ContaCorrente contaOrigem;
    private final ContaCorrente contaDestino;
    private final BigDecimal valor;
    private final LocalDateTime dataHoraSolicitacao;
    private StatusTransferencia status;
    private final List<Transferencia> transacoesGeradas = new ArrayList<>();

    public OrdemTransferencia(UUID id, ContaCorrente contaOrigem, ContaCorrente contaDestino, BigDecimal valor) {
        validarValorTransferencia(valor);
        validarContasTransferencia(contaOrigem, contaDestino);

        this.id = id;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.dataHoraSolicitacao = LocalDateTime.now();
        setStatusPendente();
    }

    public void efetivar(UUID idTransacaoDebito, UUID idTransacaoCredito) {
        try {
            setStatusConcluida();
            this.transacoesGeradas.add(gerarTransacaoDebito(idTransacaoDebito));
            this.transacoesGeradas.add(gerarTransacaoCredito(idTransacaoCredito));
        } catch (Exception e) {
            setStatusFalha();
            throw e;
        }
    }

    private Transferencia gerarTransacaoDebito(UUID uuid) {
        validarStatusTransferenciaParaGerarTransacao();
        BigDecimal saldoAnterior = this.contaOrigem.getSaldo();
        this.contaOrigem.sacar(this.valor);

        return new TransferenciaEnviada(uuid, this.contaOrigem, this.valor.negate(), saldoAnterior, this.contaOrigem.getSaldo(), this);
    }

    private Transferencia gerarTransacaoCredito(UUID uuid) {
        validarStatusTransferenciaParaGerarTransacao();
        BigDecimal saldoAnterior = this.contaDestino.getSaldo();
        this.contaDestino.depositar(this.valor);

        return new TransferenciaRecebida(uuid, this.contaDestino, this.valor, saldoAnterior, this.contaDestino.getSaldo(), this);
    }

    private void validarStatusTransferenciaParaGerarTransacao() {
        if (this.status != StatusTransferencia.CONCLUIDA)
            throw new TransferenciaStatusInvalidaException("Só é possível gerar extrato de transferência concluída.");
    }

    private static void validarContasTransferencia(ContaCorrente contaOrigem, ContaCorrente contaDestino) {
        if (contaOrigem.getId().equals(contaDestino.getId()))
            throw new NumeroContaInvalidoException("A conta de origem e destino não podem ser as mesmas.");
    }

    private static void validarValorTransferencia(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValorInvalidoException("Valor da transferência deve ser maior que zero.");
    }

    private void setStatusPendente() {
        this.status = StatusTransferencia.PENDENTE;
    }

    private void setStatusConcluida() {
        this.status = StatusTransferencia.CONCLUIDA;
    }

    private void setStatusFalha() {
        this.status = StatusTransferencia.FALHOU;
    }

    public UUID getId() {
        return id;
    }

    public List<Transferencia> getTransacoesGeradas() {
        return Collections.unmodifiableList(transacoesGeradas);
    }

    public LocalDateTime getDataHoraSolicitacao() {
        return dataHoraSolicitacao;
    }

    public ContaCorrente getContaOrigem() {
        return contaOrigem;
    }

    public ContaCorrente getContaDestino() {
        return contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public StatusTransferencia getStatus() {
        return status;
    }
}