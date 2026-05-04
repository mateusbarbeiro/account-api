package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarSaqueUseCase;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.NumeroConta;
import com.coopfinance.account_api.domain.model.transacao.Saque;

import java.math.BigDecimal;

public class RealizarSaqueService implements RealizarSaqueUseCase {

    private final TransacaoRepository transacaoRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final IdGenerator idGenerator;

    private NumeroConta numeroConta;

    public RealizarSaqueService(TransacaoRepository transacaoRepository, ContaCorrenteRepository contaCorrenteRepository, IdGenerator idGenerator) {
        this.transacaoRepository = transacaoRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Void executar(SaqueCommand input) {
        validaNumeroConta(input.numeroConta());
        ContaCorrente contaCorrente = getContaCorrente(input);

        BigDecimal saldoAnterior = contaCorrente.getSaldo();
        atualizaSaldoConta(input.valor(), contaCorrente);
        BigDecimal saldoApos = contaCorrente.getSaldo();
        geraRegistroSaque(input.valor(), contaCorrente, saldoAnterior, saldoApos);
        return null;
    }

    private ContaCorrente getContaCorrente(SaqueCommand input) {
        return contaCorrenteRepository.encontrarPorNumeroConta(numeroConta.contaBase(), numeroConta.calcularDigitoVerificador())
                .orElseThrow(() -> new ContaCorrenteNaoEncontrada("Conta não encontrada com número: " + input.numeroConta()));
    }

    private void geraRegistroSaque(BigDecimal valor, ContaCorrente conta, BigDecimal saldoAnterior, BigDecimal saldoApos) {
        Saque saque = new Saque(idGenerator.nextId(), conta, valor, saldoAnterior, saldoApos);
        transacaoRepository.salvar(saque);
    }

    private void atualizaSaldoConta(BigDecimal valor, ContaCorrente conta) {
        conta.sacar(valor);
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
