package com.coopfinance.account_api.infraestructure.config.decorator;

import com.coopfinance.account_api.application.exception.ContaCorrenteAtualizadaConcorrentementeException;
import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarTransferenciaUseCase;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;

public class RealizarTransferenciaUseCaseRetryDecorator implements RealizarTransferenciaUseCase {

    private final RealizarTransferenciaUseCase casoDeUsoPuro;

    public RealizarTransferenciaUseCaseRetryDecorator(RealizarTransferenciaUseCase casoDeUsoPuro) {
        this.casoDeUsoPuro = casoDeUsoPuro;
    }

    @Override
    @Retry(name = "transacaoRetry", fallbackMethod = "fallbackTransferencia")
    @Transactional
    public Void executar(TransferenciaCommand input) {
        return casoDeUsoPuro.executar(input);
    }

    public Void fallbackTransferencia(TransferenciaCommand input, OptimisticLockingFailureException e) {
        System.out.println("Retry falhou para transferencia nas contas origem " + input.contaOrigem() + " e destino " + input.contaDestino() + " com valor " + input.valor() + ". Tentando novamente...");
        System.out.println(e.getMessage());
        throw new ContaCorrenteAtualizadaConcorrentementeException("Falha ao sacar devido a concorrência.", e);
    }
}
