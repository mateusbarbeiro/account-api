package com.coopfinance.account_api.domain.model.operacao;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.TransferenciaStatusInvalidaException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.transacao.transferencia.Transferencia;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaEnviada;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaRecebida;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
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
    private TransferenciaRecebida transferenciaRecebida;
    private TransferenciaEnviada transferenciaEnviada;

    public OrdemTransferencia(UUID id, ContaCorrente contaOrigem, ContaCorrente contaDestino, BigDecimal valor) {
        this(id, contaOrigem, contaDestino, valor, LocalDateTime.now(), StatusTransferencia.PENDENTE);
    }

    public OrdemTransferencia(UUID id, ContaCorrente contaOrigem, ContaCorrente contaDestino, BigDecimal valor, LocalDateTime dataHoraSolicitacao, StatusTransferencia status) {
        validarValorTransferencia(valor);
        validarContasTransferencia(contaOrigem, contaDestino);

        this.id = id;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.dataHoraSolicitacao = dataHoraSolicitacao;
        this.status = status;
    }

    public void efetivar(UUID idTransacaoDebito, UUID idTransacaoCredito) {
        try {
            setStatusConcluida();
            this.transferenciaEnviada = gerarTransacaoDebito(idTransacaoDebito);
            this.transferenciaRecebida = gerarTransacaoCredito(idTransacaoCredito);
        } catch (Exception e) {
            setStatusFalha();
            throw e;
        }
    }

    private TransferenciaEnviada gerarTransacaoDebito(UUID uuid) {
        validarStatusTransferenciaParaGerarTransacao();
        BigDecimal saldoAnterior = this.contaOrigem.getSaldo();
        this.contaOrigem.sacar(this.valor);

        return new TransferenciaEnviada(uuid, this.contaOrigem, this.valor.negate(), saldoAnterior, this.contaOrigem.getSaldo(), this);
    }

    private TransferenciaRecebida gerarTransacaoCredito(UUID uuid) {
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

    private void setStatusConcluida() {
        this.status = StatusTransferencia.CONCLUIDA;
    }

    private void setStatusFalha() {
        this.status = StatusTransferencia.FALHOU;
    }

    public List<Transferencia> getTransacoesGeradas() {
        List<Transferencia> transacoes = new ArrayList<>();
        if (transferenciaEnviada != null)
            transacoes.add(transferenciaEnviada);

        if (transferenciaRecebida != null)
            transacoes.add(transferenciaRecebida);

        return transacoes;
    }
}
