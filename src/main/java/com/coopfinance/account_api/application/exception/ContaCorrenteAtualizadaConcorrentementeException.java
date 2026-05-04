package com.coopfinance.account_api.application.exception;

/**
 * Exceção lançada quando um conflito de versão ocorre durante a atualização da conta corrente
 * em um cenário de optimistic locking com múltiplas instâncias da aplicação.
 */
public class ContaCorrenteAtualizadaConcorrentementeException extends RuntimeException {

    public ContaCorrenteAtualizadaConcorrentementeException(String message, Throwable cause) {
        super(message, cause);
    }
}

