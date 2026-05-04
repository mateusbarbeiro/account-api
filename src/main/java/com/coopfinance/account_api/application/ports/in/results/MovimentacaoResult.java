package com.coopfinance.account_api.application.ports.in.results;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimentacaoResult(
        OffsetDateTime data,
        String tipo,
        BigDecimal valor
) {
}
