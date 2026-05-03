package com.coopfinance.account_api.application.usecase.mapper;

import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ContaCorrenteUseCaseMapper {

    @Mapping(target = "numeroConta", expression = "java(String.valueOf(contaCorrente.getNumeroConta()))")
    @Mapping(target = "digitoVerificador", expression = "java(String.valueOf(contaCorrente.getDigitoVerificadorConta()))")
    @Mapping(target = "cpfCnpj", expression = "java(contaCorrente.getDocumento().valor())")
    ContaCorrenteResult toResult(ContaCorrente contaCorrente);
}
