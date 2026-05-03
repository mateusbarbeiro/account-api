package com.coopfinance.account_api.infraestructure.adapters.out.uuid;

import com.coopfinance.account_api.application.ports.out.IdGenerator;
import com.fasterxml.uuid.Generators;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class UuidV7GeneratorAdapter implements IdGenerator {
    @Override
    public UUID nextId() {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
