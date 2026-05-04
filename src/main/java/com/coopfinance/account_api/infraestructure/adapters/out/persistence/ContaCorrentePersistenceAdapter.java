package com.coopfinance.account_api.infraestructure.adapters.out.persistence;

import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.ContaCorrenteEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.ContaCorrentePersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.ContaCorrenteJpaRepository;

import java.util.Optional;

public class ContaCorrentePersistenceAdapter implements ContaCorrenteRepository {
    private final ContaCorrenteJpaRepository repository;
    private final ContaCorrentePersistenceMapper mapper;

    public ContaCorrentePersistenceAdapter(ContaCorrenteJpaRepository repository, ContaCorrentePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ContaCorrente salvar(ContaCorrente contaCorrente) {
        ContaCorrenteEntity entity = repository.save(mapper.toEntity(contaCorrente));
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<ContaCorrente> encontrarPorNumeroConta(Long numeroConta, int digitoVerificador) {
        return repository.findByNumeroContaAndDigitoVerificador(numeroConta, digitoVerificador).map(mapper::toDomain);
    }

    @Override
    public Long encontraProximoNumeroConta() {
        return repository.findNextNumeroConta();
    }
}
