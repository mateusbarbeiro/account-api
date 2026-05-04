package com.coopfinance.account_api.infraestructure.adapters.in.rest.mappers;

import com.coopfinance.account_api.application.ports.in.commands.AberturaContaCorrenteCommand;
import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.AberturaContaCorrenteRequest;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.ContaCorrenteResponse;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.DepositoRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContaCorrenteRestMapper {

    AberturaContaCorrenteCommand toInput(AberturaContaCorrenteRequest request);

    ContaCorrenteResponse toOutput(ContaCorrenteResult request);
    
    DepositoCommand toInput(DepositoRequest request);
}
