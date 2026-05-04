package com.coopfinance.account_api.infraestructure.config;

import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.ContaCorrentePersistenceAdapter;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.TransacaoPersistenceAdapter;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.ContaCorrentePersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.TransacaoPersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.ContaCorrenteJpaRepository;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.TransacaoJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryBeanConfig {

    @Bean
    public ContaCorrenteRepository contaCorrenteRepository(ContaCorrenteJpaRepository jpaRepository, ContaCorrentePersistenceMapper mapper) {
        return new ContaCorrentePersistenceAdapter(jpaRepository, mapper);
    }

    @Bean
    public TransacaoRepository transacaoRepository(TransacaoJpaRepository jpaRepository, TransacaoPersistenceMapper mapper) {
        return new TransacaoPersistenceAdapter(mapper, jpaRepository);
    }
}
