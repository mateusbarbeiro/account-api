package com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper;

import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.OrdemTransferenciaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrdemTransferenciaPersistenceMapper {

    private final ContaCorrentePersistenceMapper contaCorrenteMapper;

    public OrdemTransferenciaPersistenceMapper(ContaCorrentePersistenceMapper contaCorrenteMapper) {
        this.contaCorrenteMapper = contaCorrenteMapper;
    }

    public OrdemTransferenciaEntity toEntity(OrdemTransferencia ordemTransferencia) {
        if (ordemTransferencia == null) {
            return null;
        }
        OrdemTransferenciaEntity entity = new OrdemTransferenciaEntity();
        entity.setId(ordemTransferencia.getId());
        entity.setContaOrigem(contaCorrenteMapper.toEntity(ordemTransferencia.getContaOrigem()));
        entity.setContaDestino(contaCorrenteMapper.toEntity(ordemTransferencia.getContaDestino()));
        entity.setValor(ordemTransferencia.getValor());
        entity.setDataHoraSolicitacao(ordemTransferencia.getDataHoraSolicitacao());
        if (ordemTransferencia.getStatus() != null) {
            entity.setStatus(OrdemTransferenciaEntity.StatusTransferencia.valueOf(ordemTransferencia.getStatus().name()));
        }
        return entity;
    }

    public OrdemTransferencia toDomain(OrdemTransferenciaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new OrdemTransferencia(
                entity.getId(),
                contaCorrenteMapper.toDomain(entity.getContaOrigem()),
                contaCorrenteMapper.toDomain(entity.getContaDestino()),
                entity.getValor(),
                entity.getDataHoraSolicitacao(),
                entity.getStatus() != null ? OrdemTransferencia.StatusTransferencia.valueOf(entity.getStatus().name()) : null
        );
    }
}
