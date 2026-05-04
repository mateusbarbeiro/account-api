package com.coopfinance.account_api.infraestructure.adapters.out.persistence;

import com.coopfinance.account_api.application.ports.out.repository.OrdemTransferenciaRepository;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.OrdemTransferenciaEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.OrdemTransferenciaPersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.OrdemTransferenciaJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class OrdemTransferenciaPersistenceAdapter implements OrdemTransferenciaRepository {

    private final OrdemTransferenciaPersistenceMapper mapper;
    private final OrdemTransferenciaJpaRepository repository;

    public OrdemTransferenciaPersistenceAdapter(OrdemTransferenciaPersistenceMapper mapper, OrdemTransferenciaJpaRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public OrdemTransferencia salvar(OrdemTransferencia ordemTransferencia) {
        OrdemTransferenciaEntity entity = mapper.toEntity(ordemTransferencia);
        OrdemTransferenciaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
