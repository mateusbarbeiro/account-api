package com.coopfinance.account_api.infraestructure.config;

import com.coopfinance.account_api.application.ports.out.ContaCorrenteRepository;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.ContaCorrentePersistenceAdapter;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.ContaCorrentePersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.ContaCorrenteJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryBeanConfig {

    @Bean
    public ContaCorrenteRepository contaCorrenteRepository(ContaCorrenteJpaRepository jpaRepository, ContaCorrentePersistenceMapper mapper) {
        return new ContaCorrentePersistenceAdapter(jpaRepository, mapper);
    }
}
