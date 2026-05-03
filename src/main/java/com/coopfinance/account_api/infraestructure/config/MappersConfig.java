package com.coopfinance.account_api.infraestructure.config;

import com.coopfinance.account_api.application.usecase.mapper.ContaCorrenteUseCaseMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

    @Bean
    public ContaCorrenteUseCaseMapper contaCorrenteUseCaseMapper() {
        return Mappers.getMapper(ContaCorrenteUseCaseMapper.class);
    }
}
