package com.coopfinance.account_api.application.ports.in.commands;

import java.math.BigDecimal;

public record TransferenciaCommand(
        String contaOrigem,
        String contaDestino,
        BigDecimal valor
) {
}
