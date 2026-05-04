package com.coopfinance.account_api.infraestructure.config.decorator;

import com.coopfinance.account_api.application.exception.ContaCorrenteAtualizadaConcorrentementeException;
import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarDepositoUseCase;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class RealizarDepositoUseCaseRetryDecorator implements RealizarDepositoUseCase {

    private final RealizarDepositoUseCase casoDeUsoPuro;

    public RealizarDepositoUseCaseRetryDecorator(RealizarDepositoUseCase casoDeUsoPuro) {
        this.casoDeUsoPuro = casoDeUsoPuro;
    }

    @Override
    @Retry(name = "transacaoRetry", fallbackMethod = "fallbackDeposito")
    @Transactional
    public Void executar(DepositoCommand input) {
        return casoDeUsoPuro.executar(input);
    }

    public Void fallbackDeposito(DepositoCommand input, OptimisticLockingFailureException e) {
        System.out.println("Retry falhou para depósito na conta " + input.numeroConta() + " com valor " + input.valor() + ". Tentando novamente...");
        System.out.println(e.getMessage());
        throw new ContaCorrenteAtualizadaConcorrentementeException("Falha ao depositar devido a concorrência.", e);
    }
}
