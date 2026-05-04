package com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper;

import com.coopfinance.account_api.domain.model.transacao.Deposito;
import com.coopfinance.account_api.domain.model.transacao.Saque;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaEnviada;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaRecebida;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.DepositoEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.SaqueEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.TransferenciaEnviadaEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.TransferenciaRecebidaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ContaCorrentePersistenceMapper.class)
public interface TransacaoPersistenceMapper {

    DepositoEntity toEntity(Deposito transacao);

    SaqueEntity toEntity(Saque transacao);

    TransferenciaEnviadaEntity toEntity(TransferenciaEnviada transacao);

    TransferenciaRecebidaEntity toEntity(TransferenciaRecebida transacao);
}
