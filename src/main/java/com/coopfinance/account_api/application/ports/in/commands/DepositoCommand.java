package com.coopfinance.account_api.application.ports.in.commands;

import java.math.BigDecimal;

public record DepositoCommand(String numeroConta, BigDecimal valor) {
}
