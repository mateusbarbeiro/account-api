package com.coopfinance.account_api.application.exception;

public class ContaCorrenteNaoEncontrada extends RuntimeException {
    public ContaCorrenteNaoEncontrada(String message) {
        super(message);
    }
}
