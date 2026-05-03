package com.coopfinance.account_api.application.ports.in.usecase;

public interface UseCase<I, O> {
    O executar(I input);
}