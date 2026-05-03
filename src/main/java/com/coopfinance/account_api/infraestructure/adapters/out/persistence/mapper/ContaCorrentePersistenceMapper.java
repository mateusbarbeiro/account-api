package com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper;

import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.ContaCorrenteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContaCorrentePersistenceMapper {

    @Mapping(target = "numeroConta", source = "numeroConta")
    @Mapping(target = "digitoVerificador", source = "digitoVerificadorConta")
    @Mapping(target = "documento", expression = "java(domain.getDocumento().valor())")
    ContaCorrenteEntity toEntity(ContaCorrente domain);

    default ContaCorrente toDomain(ContaCorrenteEntity entity) {
        return new ContaCorrente(
                entity.getId(),
                entity.getNumeroConta(),
                entity.getDigitoVerificador(),
                entity.getDocumento(),
                entity.getSaldo(),
                entity.getVersao()
        );
    }

}