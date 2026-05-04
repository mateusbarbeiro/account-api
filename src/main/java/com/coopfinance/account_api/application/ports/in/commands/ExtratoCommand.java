package com.coopfinance.account_api.application.ports.in.commands;

import java.time.LocalDate;

public record ExtratoCommand(
        String numeroConta,
        LocalDate dataInicio,
        LocalDate dataFim
) {
}
