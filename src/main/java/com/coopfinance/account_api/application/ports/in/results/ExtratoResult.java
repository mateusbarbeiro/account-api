package com.coopfinance.account_api.application.ports.in.results;

import java.math.BigDecimal;
import java.util.List;

public record ExtratoResult(
        BigDecimal saldoTotal,
        List<MovimentacaoResult> movimentacoes
) {
}
