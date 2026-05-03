package com.coopfinance.account_api.application.ports.out;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;

public interface ContaCorrenteRepository {
    ContaCorrente salvar(ContaCorrente contaCorrente);
    Long encontraProximoNumeroConta();
}
