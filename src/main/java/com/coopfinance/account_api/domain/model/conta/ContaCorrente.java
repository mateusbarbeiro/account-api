package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ContaCorrente {

    private final UUID id;
    private final NumeroConta numero;
    private final Documento documento;
    private BigDecimal saldo;
    private final Long versao;

    public ContaCorrente(UUID id, Long numero, String documento, BigDecimal saldo, Long versao) {
        this.id = id;
        this.numero = new NumeroConta(numero);
        this.documento = new Documento(documento);
        this.saldo = saldo;
        this.versao = versao;
    }

    public ContaCorrente(UUID id, Long numero, int digito, String documento, BigDecimal saldo, Long versao) {
        this(id, numero, documento, saldo, versao);
        validarDigitoVerificador(digito);
    }

    public ContaCorrente(UUID id, Long numero, String documento) {
        this(id, numero, documento, BigDecimal.ZERO, 0L);
    }

    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor);
        validarSaldoSuficiente(valor);

        saldo = saldo.subtract(valor);
    }

    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor);
        this.saldo = this.saldo.add(valor);
    }

    private void validarSaldoSuficiente(BigDecimal valor) {
        if (saldo.compareTo(valor) < 0)
            throw new SaldoInsuficienteException("Saldo insuficiente.");
    }

    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValorInvalidoException("O valor da operação deve ser maior que zero.");

        if (valor.compareTo(BigDecimal.valueOf(0.01)) < 0)
            throw new ValorInvalidoException("O valor da operação deve ser no mínimo R$ 0,01.");
    }

    private void validarDigitoVerificador(int digito) {
        if (digito != this.numero.calcularDigitoVerificador())
            throw new NumeroContaInvalidoException("Dígito verificador inválido.");
    }

    public Long getNumeroConta() { return numero.contaBase(); }
    public int getDigitoVerificadorConta() { return numero.calcularDigitoVerificador(); }
    public String getNumeroContaComposto() {
        return numero.contaComposta();
    }
}