package com.coopfinance.account_api.infraestructure.adapters.in.rest;

import com.coopfinance.account_api.application.ports.in.commands.AberturaContaCorrenteCommand;
import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.application.ports.in.usecase.AberturaContaCorrenteUseCase;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarDepositoUseCase;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarSaqueUseCase;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarTransferenciaUseCase;
import com.coopfinance.account_api.infraestructure.adapters.in.rest.mappers.ContaCorrenteRestMapper;
import com.coopfinance.account_api.infrastructure.api.rest.generated.ContaCorrenteApi;
import com.coopfinance.account_api.infrastructure.api.rest.generated.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class ContaCorrenteController implements ContaCorrenteApi {

    private final AberturaContaCorrenteUseCase aberturaContaCorrenteUseCase;
    private final RealizarDepositoUseCase realizarDepositoUseCase;
    private final RealizarSaqueUseCase realizarSaqueUseCase;
    private final RealizarTransferenciaUseCase realizarTransferenciaUseCase;
    private final ContaCorrenteRestMapper mapper;

    public ContaCorrenteController(
            AberturaContaCorrenteUseCase aberturaContaCorrenteUseCase,
            RealizarDepositoUseCase realizarDepositoUseCase,
            RealizarSaqueUseCase realizarSaqueUseCase,
            RealizarTransferenciaUseCase realizarTransferenciaUseCase,
            ContaCorrenteRestMapper mapper) {
        this.aberturaContaCorrenteUseCase = aberturaContaCorrenteUseCase;
        this.realizarDepositoUseCase = realizarDepositoUseCase;
        this.realizarSaqueUseCase = realizarSaqueUseCase;
        this.realizarTransferenciaUseCase = realizarTransferenciaUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<ContaCorrenteResponse> abrirConta(AberturaContaCorrenteRequest aberturaContaRequest) {
        AberturaContaCorrenteCommand input = mapper.toInput(aberturaContaRequest);
        ContaCorrenteResult result = aberturaContaCorrenteUseCase.executar(input);

        return ResponseEntity.status(CREATED).body(mapper.toOutput(result));
    }

    @Override
    public ResponseEntity<Void> realizarDeposito(DepositoRequest depositoRequest) {
        DepositoCommand input = mapper.toInput(depositoRequest);
        realizarDepositoUseCase.executar(input);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> realizarSaque(SaqueRequest saqueRequest) {
        SaqueCommand input = mapper.toInput(saqueRequest);
        realizarSaqueUseCase.executar(input);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> realizarTransferencia(TransferenciaRequest transferenciaRequest) {
        TransferenciaCommand input = mapper.toInput(transferenciaRequest);
        realizarTransferenciaUseCase.executar(input);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ExtratoResponse> consultarExtrato(String numeroConta, LocalDate dataInicio, LocalDate dataFim) {
        return null;
    }
}
