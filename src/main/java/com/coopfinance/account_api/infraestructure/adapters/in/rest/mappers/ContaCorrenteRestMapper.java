package com.coopfinance.account_api.infraestructure.adapters.in.rest.mappers;

import com.coopfinance.account_api.application.ports.in.commands.AberturaContaCorrenteCommand;
import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.application.ports.in.results.ExtratoResult;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContaCorrenteRestMapper {

    AberturaContaCorrenteCommand toInput(AberturaContaCorrenteRequest request);

    ContaCorrenteResponse toOutput(ContaCorrenteResult request);
    
    DepositoCommand toInput(DepositoRequest request);

    SaqueCommand toInput(SaqueRequest request);

    TransferenciaCommand toInput(TransferenciaRequest request);

    ExtratoResponse toOutput(ExtratoResult result);
}
