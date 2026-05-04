package com.coopfinance.account_api.application.ports.out.repository;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import java.util.Optional;

public interface ContaCorrenteRepository {
    ContaCorrente salvar(ContaCorrente contaCorrente);
    Optional<ContaCorrente> encontrarPorNumeroConta(Long numeroConta, int digitoVerificador);
    Long encontraProximoNumeroConta();
}