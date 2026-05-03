package com.coopfinance.account_api.infraestructure.adapters.out.persistence;

import com.coopfinance.account_api.application.ports.out.ContaCorrenteRepository;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.ContaCorrenteEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.ContaCorrentePersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.ContaCorrenteJpaRepository;

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
    public Long encontraProximoNumeroConta() {
        return repository.findNextNumeroConta();
    }
}
