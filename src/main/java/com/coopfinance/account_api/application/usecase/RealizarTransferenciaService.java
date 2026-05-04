package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarTransferenciaUseCase;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.OrdemTransferenciaRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.conta.NumeroConta;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;

public class RealizarTransferenciaService implements RealizarTransferenciaUseCase {

    private final TransacaoRepository transacaoRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final OrdemTransferenciaRepository ordemTransferenciaRepository;
    private final IdGenerator idGenerator;

    public RealizarTransferenciaService(TransacaoRepository transacaoRepository,
                                        ContaCorrenteRepository contaCorrenteRepository,
                                        OrdemTransferenciaRepository ordemTransferenciaRepository,
                                        IdGenerator idGenerator) {
        this.transacaoRepository = transacaoRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.ordemTransferenciaRepository = ordemTransferenciaRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Void executar(TransferenciaCommand input) {
        ContaCorrente contaOrigem = getContaCorrente(input.contaOrigem());
        ContaCorrente contaDestino = getContaCorrente(input.contaDestino());

        OrdemTransferencia ordemTransferencia = new OrdemTransferencia(
                idGenerator.nextId(),
                contaOrigem,
                contaDestino,
                input.valor()
        );

        ordemTransferencia = ordemTransferenciaRepository.salvar(ordemTransferencia);
        ordemTransferencia.efetivar(idGenerator.nextId(), idGenerator.nextId());
        ordemTransferenciaRepository.salvar(ordemTransferencia);

        contaCorrenteRepository.salvar(ordemTransferencia.getContaOrigem());
        contaCorrenteRepository.salvar(ordemTransferencia.getContaDestino());

        transacaoRepository.salvar(ordemTransferencia.getTransferenciaEnviada());
        transacaoRepository.salvar(ordemTransferencia.getTransferenciaRecebida());

        return null;
    }

    private ContaCorrente getContaCorrente(String numeroComposto) {
        validaNumeroConta(numeroComposto);
        String[] partes = numeroComposto.split("-");
        Long numero = Long.parseLong(partes[0]);
        int digito = Integer.parseInt(partes[1]);
        
        return contaCorrenteRepository.encontrarPorNumeroConta(numero, digito)
                .orElseThrow(() -> new ContaCorrenteNaoEncontrada("Conta não encontrada com número: " + numeroComposto));
    }

    private void validaNumeroConta(String numeroComposto) {
        if (numeroComposto == null || numeroComposto.isEmpty())
            throw new NumeroContaInvalidoException("Número da conta não pode ser vazio.");

        String[] numeroContaPartes = numeroComposto.split("-");
        if (numeroContaPartes.length != 2)
            throw new NumeroContaInvalidoException("Digito verificador deve ser informado.");

        try {
            NumeroConta numeroConta = new NumeroConta(Long.parseLong(numeroContaPartes[0]));
            if (numeroConta.calcularDigitoVerificador() != Integer.parseInt(numeroContaPartes[1])) {
                throw new NumeroContaInvalidoException("Dígito verificador inválido para o número da conta: " + numeroComposto);
            }
        } catch (NumberFormatException e) {
            throw new NumeroContaInvalidoException("Número de conta inválido: " + numeroComposto + ". Erro: " + e.getMessage());
        }
    }
}
