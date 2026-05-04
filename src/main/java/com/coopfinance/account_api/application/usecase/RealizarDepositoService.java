package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarDepositoUseCase;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.NumeroConta;
import com.coopfinance.account_api.domain.model.transacao.Deposito;

import java.math.BigDecimal;

public class RealizarDepositoService implements RealizarDepositoUseCase {

    private final TransacaoRepository transacaoRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final IdGenerator idGenerator;

    private NumeroConta numeroConta;

    public RealizarDepositoService(TransacaoRepository transacaoRepository, ContaCorrenteRepository contaCorrenteRepository, IdGenerator idGenerator) {
        this.transacaoRepository = transacaoRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Void executar(DepositoCommand input) {
        validaNumeroConta(input.numeroConta());
        ContaCorrente contaCorrente = getContaCorrente(input);

        BigDecimal saldoAnterior = contaCorrente.getSaldo();
        atualizaSaldoConta(input.valor(), contaCorrente);
        BigDecimal saldoApos = contaCorrente.getSaldo();
        geraRegistroDeposito(input.valor(), contaCorrente, saldoAnterior, saldoApos);
        return null;
    }

    private ContaCorrente getContaCorrente(DepositoCommand input) {
        return contaCorrenteRepository.encontrarPorNumeroConta(numeroConta.contaBase(), numeroConta.calcularDigitoVerificador())
                .orElseThrow(() -> new ContaCorrenteNaoEncontrada("Conta não encontrada com número: " + input.numeroConta()));
    }

    private void geraRegistroDeposito(BigDecimal valor, ContaCorrente conta, BigDecimal saldoAnterior, BigDecimal saldoApos) {
        Deposito deposito = new Deposito(idGenerator.nextId(), conta, valor, saldoAnterior, saldoApos);
        transacaoRepository.salvar(deposito);
    }

    private void atualizaSaldoConta(BigDecimal valor, ContaCorrente conta) {
        conta.depositar(valor);
        contaCorrenteRepository.salvar(conta);
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

