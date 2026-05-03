package com.coopfinance.account_api.infraestructure.config;

import com.coopfinance.account_api.application.ports.in.usecase.AberturaContaCorrenteUseCase;
import com.coopfinance.account_api.application.ports.out.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.IdGenerator;
import com.coopfinance.account_api.application.ports.out.NumeroContaGenerator;
import com.coopfinance.account_api.application.usecase.AberturaContaCorrenteService;
import com.coopfinance.account_api.application.usecase.mapper.ContaCorrenteUseCaseMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseBeanConfig {

    @Bean
    public AberturaContaCorrenteUseCase aberturaContaCorrenteUseCase(ContaCorrenteRepository repository, IdGenerator idGenerator, ContaCorrenteUseCaseMapper mapper, NumeroContaGenerator numeroContaGenerator) {
        return new AberturaContaCorrenteService(repository, idGenerator, mapper, numeroContaGenerator);
    }
}
