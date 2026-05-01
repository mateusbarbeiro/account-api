package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.DocumentoInvalidoException;

public record Documento(String valor) {

    public Documento {
        validarConsistencia(valor);
    }

    private static void validarConsistencia(String valor) {
        validarVazioOuNulo(valor);
        validarFormatoValido(valor);
    }

    private static void validarVazioOuNulo(String valor) {
        if (valor == null || valor.isBlank())
            throw new DocumentoInvalidoException("O documento não pode ser vazio.");
    }

    private static void validarFormatoValido(String valor) {
        if (!isCpfOuCnpjValido(valor))
            throw new DocumentoInvalidoException("CPF ou CNPJ inválido.");
    }

    private static boolean isCpfOuCnpjValido(String documento) {
        String documentoLimpo = documento.replaceAll("[^0-9]", "");
        return documentoLimpo.length() == 11 || documentoLimpo.length() == 14;
    }
}
