package com.coopfinance.account_api.infraestructure.adapters.out.uuid;

import com.coopfinance.account_api.domain.ports.out.IdGeneratorPort;
import com.fasterxml.uuid.Generators;

import java.util.UUID;

public class UuidV7GeneratorAdapter implements IdGeneratorPort {
    @Override
    public UUID nextId() {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
