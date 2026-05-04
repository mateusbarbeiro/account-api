package com.coopfinance.account_api.infraestructure.config;

import com.coopfinance.account_api.application.ports.in.usecase.AberturaContaCorrenteUseCase;
import com.coopfinance.account_api.application.ports.in.usecase.ConsultarExtratoUseCase;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.generator.NumeroContaGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.OrdemTransferenciaRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.application.usecase.*;
import com.coopfinance.account_api.application.usecase.mapper.ContaCorrenteUseCaseMapper;
import com.coopfinance.account_api.infraestructure.config.decorator.RealizarDepositoUseCaseRetryDecorator;
import com.coopfinance.account_api.infraestructure.config.decorator.RealizarSaqueUseCaseRetryDecorator;
import com.coopfinance.account_api.infraestructure.config.decorator.RealizarTransferenciaUseCaseRetryDecorator;
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

    @Bean
    public RealizarSaqueService realizarSaqueService(TransacaoRepository transacaoRepository, ContaCorrenteRepository repository, IdGenerator idGenerator) {
        return new RealizarSaqueService(transacaoRepository, repository, idGenerator);
    }

    @Bean
    @Primary
    public RealizarSaqueUseCaseRetryDecorator realizarSaqueUseCaseComRetry(RealizarSaqueService casoDeUsoPuro) {
        return new RealizarSaqueUseCaseRetryDecorator(casoDeUsoPuro);
    }

    @Bean
    public RealizarTransferenciaService realizarTransferenciaService(TransacaoRepository transacaoRepository, ContaCorrenteRepository repository, OrdemTransferenciaRepository ordemTransferenciaRepository, IdGenerator idGenerator) {
        return new RealizarTransferenciaService(transacaoRepository, repository, ordemTransferenciaRepository, idGenerator);
    }

    @Bean
    @Primary
    public RealizarTransferenciaUseCaseRetryDecorator realizarTransferenciaUseCaseComRetry(RealizarTransferenciaService casoDeUsoPuro) {
        return new RealizarTransferenciaUseCaseRetryDecorator(casoDeUsoPuro);
    }

    @Bean
    public ConsultarExtratoUseCase consultarExtratoUseCase(ContaCorrenteRepository contaCorrenteRepository, TransacaoRepository transacaoRepository) {
        return new ConsultarExtratoService(contaCorrenteRepository, transacaoRepository);
    }
}
