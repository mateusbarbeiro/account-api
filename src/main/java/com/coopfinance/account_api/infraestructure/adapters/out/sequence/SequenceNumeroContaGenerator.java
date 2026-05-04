package com.coopfinance.account_api.infraestructure.adapters.out.sequence;

import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.generator.NumeroContaGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SequenceNumeroContaGenerator implements NumeroContaGenerator {

    private final ContaCorrenteRepository repository;

    @Value("${app.sequence.allocation-size:50}")
    private int allocationSize;
    
    private Long currentValue;
    private Long maxValue;

    public SequenceNumeroContaGenerator(ContaCorrenteRepository repository) {
        this.repository = repository;
    }

    @Override
    public synchronized Long getNextNumeroConta() {
        if (deveBuscarProximoIntervaloSequence())
            atualizaComProximoIntervalo();

        return currentValue++;
    }

    private void atualizaComProximoIntervalo() {
        Long nextVal = repository.encontraProximoNumeroConta();
        currentValue = nextVal;
        maxValue = nextVal + allocationSize;
    }

    private boolean deveBuscarProximoIntervaloSequence() {
        return currentValue == null || currentValue >= maxValue;
    }
}
