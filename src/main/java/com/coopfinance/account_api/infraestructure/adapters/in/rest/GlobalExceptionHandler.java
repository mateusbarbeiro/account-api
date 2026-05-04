package com.coopfinance.account_api.infraestructure.adapters.in.rest;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.exception.ContaCorrenteAtualizadaConcorrentementeException;
import com.coopfinance.account_api.domain.exception.DocumentoInvalidoException;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.exception.TransferenciaStatusInvalidaException;
import com.coopfinance.account_api.domain.exception.ValorInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            DocumentoInvalidoException.class,
            NumeroContaInvalidoException.class,
            ValorInvalidoException.class,
            TransferenciaStatusInvalidaException.class
    })
    public ResponseEntity<Object> handleBadRequestExceptions(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Object> handleUnprocessableEntityExceptions(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(ContaCorrenteAtualizadaConcorrentementeException.class)
    public ResponseEntity<Object> handleConflictException(ContaCorrenteAtualizadaConcorrentementeException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        System.out.println("Erro inesperado: " + ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor.");
    }

    @ExceptionHandler(ContaCorrenteNaoEncontrada.class)
    public ResponseEntity<Object> handleNotFound(ContaCorrenteNaoEncontrada ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}
