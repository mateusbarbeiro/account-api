package com.coopfinance.account_api.application.ports.out.repository;

import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;

public interface OrdemTransferenciaRepository {
    OrdemTransferencia salvar(OrdemTransferencia ordemTransferencia);
}
