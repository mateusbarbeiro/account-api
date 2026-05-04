package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.ExtratoCommand;
import com.coopfinance.account_api.application.ports.in.results.ExtratoResult;
import com.coopfinance.account_api.application.ports.in.usecase.ConsultarExtratoUseCase;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.NumeroConta;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class ConsultarExtratoService implements ConsultarExtratoUseCase {
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final TransacaoRepository transacaoRepository;

    private NumeroConta numeroConta;

    public ConsultarExtratoService(ContaCorrenteRepository contaCorrenteRepository, TransacaoRepository transacaoRepository) {
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    public ExtratoResult executar(ExtratoCommand input) {
        validaNumeroConta(input.numeroConta());
        ContaCorrente contaCorrente = getContaCorrente(input);

        LocalDate inicioPeriodo = input.dataInicio() == null ? LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()) : input.dataInicio();
        LocalDate fimPeriodo = input.dataFim() == null ? LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()) : input.dataFim();
        return new ExtratoResult(
                contaCorrente.getSaldo(),
                transacaoRepository.listarTransacoesPorConta(contaCorrente.getId(), inicioPeriodo, fimPeriodo)
        );
    }

    private ContaCorrente getContaCorrente(ExtratoCommand input) {
        return contaCorrenteRepository.encontrarPorNumeroConta(numeroConta.contaBase(), numeroConta.calcularDigitoVerificador())
                .orElseThrow(() -> new ContaCorrenteNaoEncontrada("Conta não encontrada com número: " + input.numeroConta()));
    }

    private void validaNumeroConta(String numeroComposto) {
        if (numeroComposto == null || numeroComposto.isEmpty())
            throw new NumeroContaInvalidoException("Número da conta não pode ser vazio.");

        String[] numeroContaPartes = numeroComposto.split("-");
        if (numeroContaPartes.length != 2)
            throw new NumeroContaInvalidoException("Digito verificador deve ser informado.");

        try {
            validaDigitoVerificador(numeroComposto, numeroContaPartes);
        } catch (NumberFormatException e) {
            throw new NumeroContaInvalidoException("Número de conta inválido: " + numeroComposto + ". Erro: " + e.getMessage());
        }
    }

    private void validaDigitoVerificador(String numeroComposto, String[] numeroContaPartes) {
        this.numeroConta = new NumeroConta(Long.parseLong(numeroContaPartes[0]));
        if (numeroConta.calcularDigitoVerificador() != Integer.parseInt(numeroContaPartes[1]))
            throw new NumeroContaInvalidoException("Dígito verificador inválido para o número da conta: " + numeroComposto);
    }

}
