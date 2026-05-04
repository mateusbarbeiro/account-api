package com.coopfinance.account_api.infraestructure.config.decorator;

import com.coopfinance.account_api.application.exception.ContaCorrenteAtualizadaConcorrentementeException;
import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarSaqueUseCase;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class RealizarSaqueUseCaseRetryDecorator implements RealizarSaqueUseCase {

    private final RealizarSaqueUseCase casoDeUsoPuro;

    public RealizarSaqueUseCaseRetryDecorator(RealizarSaqueUseCase casoDeUsoPuro) {
        this.casoDeUsoPuro = casoDeUsoPuro;
    }

    @Override
    @Retry(name = "transacaoRetry", fallbackMethod = "fallbackSaque")
    @Transactional
    public Void executar(SaqueCommand input) {
        return casoDeUsoPuro.executar(input);
    }

    public Void fallbackSaque(SaqueCommand input, OptimisticLockingFailureException e) {
        System.out.println("Retry falhou para saque na conta " + input.numeroConta() + " com valor " + input.valor() + ". Tentando novamente...");
        System.out.println(e.getMessage());
        throw new ContaCorrenteAtualizadaConcorrentementeException("Falha ao sacar devido a concorrência.", e);
    }
}
