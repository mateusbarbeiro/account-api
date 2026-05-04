package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.ports.in.commands.AberturaContaCorrenteCommand;
import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.application.ports.in.usecase.AberturaContaCorrenteUseCase;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.generator.NumeroContaGenerator;
import com.coopfinance.account_api.application.usecase.mapper.ContaCorrenteUseCaseMapper;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;

public class AberturaContaCorrenteService implements AberturaContaCorrenteUseCase {

    private final ContaCorrenteRepository contaCorrenteRepository;
    private final IdGenerator idGenerator;
    private final ContaCorrenteUseCaseMapper mapper;
    private final NumeroContaGenerator numeroContaGenerator;

    public AberturaContaCorrenteService(ContaCorrenteRepository contaCorrenteRepository, IdGenerator idGeradorPort, ContaCorrenteUseCaseMapper mapper, NumeroContaGenerator numeroContaGenerator) {
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.idGenerator = idGeradorPort;
        this.mapper = mapper;
        this.numeroContaGenerator = numeroContaGenerator;
    }

    @Override
    public ContaCorrenteResult executar(AberturaContaCorrenteCommand input) {
        ContaCorrente contaSalva = contaCorrenteRepository.salvar(
                new ContaCorrente(idGenerator.nextId(), numeroContaGenerator.getNextNumeroConta(), input.cpfCnpj())
        );
        return mapper.toResult(contaSalva);
    }
}
