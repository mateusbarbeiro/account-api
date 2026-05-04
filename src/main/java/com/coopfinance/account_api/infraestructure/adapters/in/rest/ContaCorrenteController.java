package com.coopfinance.account_api.infraestructure.adapters.in.rest;

import com.coopfinance.account_api.application.ports.in.commands.*;
import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.application.ports.in.results.ExtratoResult;
import com.coopfinance.account_api.application.ports.in.usecase.*;
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
    private final ConsultarExtratoUseCase consultarExtratoUseCase;

    public ContaCorrenteController(
            AberturaContaCorrenteUseCase aberturaContaCorrenteUseCase,
            RealizarDepositoUseCase realizarDepositoUseCase,
            RealizarSaqueUseCase realizarSaqueUseCase,
            RealizarTransferenciaUseCase realizarTransferenciaUseCase,
            ContaCorrenteRestMapper mapper,
            ConsultarExtratoUseCase consultarExtratoUseCase) {
        this.aberturaContaCorrenteUseCase = aberturaContaCorrenteUseCase;
        this.realizarDepositoUseCase = realizarDepositoUseCase;
        this.realizarSaqueUseCase = realizarSaqueUseCase;
        this.realizarTransferenciaUseCase = realizarTransferenciaUseCase;
        this.mapper = mapper;
        this.consultarExtratoUseCase = consultarExtratoUseCase;
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
        ExtratoCommand extratoCommand = new ExtratoCommand(numeroConta, dataInicio, dataFim);
        ExtratoResult result = consultarExtratoUseCase.executar(extratoCommand);

        return ResponseEntity.ok().body(mapper.toOutput(result));
    }
}
