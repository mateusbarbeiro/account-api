package com.coopfinance.account_api.domain.model.conta;

import com.coopfinance.account_api.domain.exception.DocumentoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoTest {

    @Test
    void deveCriarDocumentoComCpfValido() {
        String cpf = "12345678901";
        Documento documento = new Documento(cpf);
        assertEquals(cpf, documento.valor());
    }

    @Test
    void deveCriarDocumentoComCpfFormatadoValido() {
        String cpf = "123.456.789-01";
        Documento documento = new Documento(cpf);
        assertEquals(cpf, documento.valor());
    }

    @Test
    void deveCriarDocumentoComCnpjValido() {
        String cnpj = "12345678000190";
        Documento documento = new Documento(cnpj);
        assertEquals(cnpj, documento.valor());
    }

    @Test
    void deveCriarDocumentoComCnpjFormatadoValido() {
        String cnpj = "12.345.678/0001-90";
        Documento documento = new Documento(cnpj);
        assertEquals(cnpj, documento.valor());
    }

    @Test
    void naoDeveCriarDocumentoNulo() {
        DocumentoInvalidoException exception = assertThrows(DocumentoInvalidoException.class, () -> new Documento(null));
        assertEquals("O documento não pode ser vazio.", exception.getMessage());
    }

    @Test
    void naoDeveCriarDocumentoVazio() {
        DocumentoInvalidoException exception = assertThrows(DocumentoInvalidoException.class, () -> new Documento(""));
        assertEquals("O documento não pode ser vazio.", exception.getMessage());
    }

    @Test
    void naoDeveCriarDocumentoEmBranco() {
        DocumentoInvalidoException exception = assertThrows(DocumentoInvalidoException.class, () -> new Documento("   "));
        assertEquals("O documento não pode ser vazio.", exception.getMessage());
    }

    @Test
    void naoDeveCriarDocumentoComTamanhoInvalido() {
        DocumentoInvalidoException exception = assertThrows(DocumentoInvalidoException.class, () -> new Documento("1234567890")); // 10 dígitos
        assertEquals("CPF ou CNPJ inválido.", exception.getMessage());
        
        DocumentoInvalidoException exception2 = assertThrows(DocumentoInvalidoException.class, () -> new Documento("1234567890123")); // 13 dígitos
        assertEquals("CPF ou CNPJ inválido.", exception2.getMessage());
        
        DocumentoInvalidoException exception3 = assertThrows(DocumentoInvalidoException.class, () -> new Documento("123456789012345")); // 15 dígitos
        assertEquals("CPF ou CNPJ inválido.", exception3.getMessage());
    }
}
