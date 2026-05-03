package com.coopfinance.account_api.application.ports.in.results;

public record ContaCorrenteResult(
    String numeroConta,
    String digitoVerificador,
    String cpfCnpj
) {}
