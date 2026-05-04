package com.coopfinance.account_api.application.ports.out.repository;

import com.coopfinance.account_api.domain.model.transacao.Deposito;

public interface TransacaoRepository {
    void salvar(Deposito deposito);
}
