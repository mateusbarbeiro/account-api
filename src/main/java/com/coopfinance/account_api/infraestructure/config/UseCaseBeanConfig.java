package com.coopfinance.account_api.infraestructure.config;

import com.coopfinance.account_api.application.ports.in.usecase.AberturaContaCorrenteUseCase;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.generator.NumeroContaGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.application.usecase.AberturaContaCorrenteService;
import com.coopfinance.account_api.application.usecase.RealizarDepositoService;
import com.coopfinance.account_api.application.usecase.mapper.ContaCorrenteUseCaseMapper;
import com.coopfinance.account_api.infraestructure.config.decorator.RealizarDepositoUseCaseRetryDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class UseCaseBeanConfig {

    @Bean
    public AberturaContaCorrenteUseCase aberturaContaCorrenteUseCase(ContaCorrenteRepository repository, IdGenerator idGenerator, ContaCorrenteUseCaseMapper mapper, NumeroContaGenerator numeroContaGenerator) {
        return new AberturaContaCorrenteService(repository, idGenerator, mapper, numeroContaGenerator);
    }

    @Bean
    public RealizarDepositoService realizarDepositoUseCase(TransacaoRepository transacaoRepository, ContaCorrenteRepository repository, IdGenerator idGenerator) {
        return new RealizarDepositoService(transacaoRepository, repository, idGenerator);
    }

    @Bean
    @Primary
    public RealizarDepositoUseCaseRetryDecorator realizarDepositoUseCaseComRetry(RealizarDepositoService casoDeUsoPuro) {
        return new RealizarDepositoUseCaseRetryDecorator(casoDeUsoPuro);
    }
}
