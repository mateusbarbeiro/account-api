package com.coopfinance.account_api.application.ports.out.repository;

import com.coopfinance.account_api.domain.model.transacao.Deposito;
import com.coopfinance.account_api.domain.model.transacao.Saque;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaEnviada;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaRecebida;

public interface TransacaoRepository {
    void salvar(Deposito deposito);
    void salvar(Saque saque);
    void salvar(TransferenciaRecebida saque);
    void salvar(TransferenciaEnviada saque);
}
