package com.coopfinance.account_api.infraestructure.adapters.out.persistence;

import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.model.transacao.Deposito;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.TransacaoEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.TransacaoPersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.TransacaoJpaRepository;

public class TransacaoPersistenceAdapter implements TransacaoRepository {

    private final TransacaoPersistenceMapper mapper;
    private final TransacaoJpaRepository repository;

    public TransacaoPersistenceAdapter(TransacaoPersistenceMapper mapper, TransacaoJpaRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public void salvar(Deposito deposito) {
        TransacaoEntity entity = mapper.toEntity(deposito);
        repository.save(entity);
    }
}
