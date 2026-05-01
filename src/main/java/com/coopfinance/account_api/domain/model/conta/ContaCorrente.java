package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;

import java.math.BigDecimal;
import java.util.UUID;

public class ContaCorrente {

    private final UUID id;
    private final NumeroConta numero;
    private final Documento documento;
    private BigDecimal saldo;
    private final Long versao;

    public ContaCorrente(UUID id, String numero, Documento documento) {
        this.id = id;
        this.numero = new NumeroConta(numero);
        this.documento = documento;
        this.saldo = BigDecimal.ZERO;
        this.versao = 0L;
    }

    public ContaCorrente(UUID id, String numero, String digito, Documento documento, BigDecimal saldo, Long versao) {
        this.numero = new NumeroConta(numero);
        validarDigitoVerificador(digito);
        this.id = id;
        this.documento = documento;
        this.saldo = saldo;
        this.versao = versao;
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
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("O valor da operação deve ser maior que zero.");
        }
    }

    private void validarDigitoVerificador(String digito) {
        if (Integer.parseInt(digito) != this.numero.calcularDigitoVerificador())
            throw new NumeroContaInvalidoException("Dígito verificador inválido.");
    }

    public UUID getId() { return id; }
    public String getNumeroConta() { return numero.contaBase(); }
    public String getDigitoVerificadorConta() { return String.valueOf(numero.calcularDigitoVerificador()); }
    public String getNumeroContaComposto() {
        return numero.contaComposta();
    }
    public Documento getDocumento() {
        return documento;
    }
    public BigDecimal getSaldo() { return saldo; }
    public Long getVersao() { return versao; }
}
