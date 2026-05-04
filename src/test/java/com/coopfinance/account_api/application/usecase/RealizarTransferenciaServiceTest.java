package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.OrdemTransferenciaRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.operacao.OrdemTransferencia;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaEnviada;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaRecebida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealizarTransferenciaServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private OrdemTransferenciaRepository ordemTransferenciaRepository;

    @Mock
    private IdGenerator idGenerator;

    private RealizarTransferenciaService service;

    private TransferenciaCommand command;
    private ContaCorrente contaOrigem;
    private ContaCorrente contaDestino;
    private UUID ordemId;
    private UUID envioId;
    private UUID recebimentoId;

    @BeforeEach
    void setUp() {
        service = new RealizarTransferenciaService(transacaoRepository, contaCorrenteRepository, ordemTransferenciaRepository, idGenerator);
        
        UUID contaOrigemId = UUID.randomUUID();
        UUID contaDestinoId = UUID.randomUUID();
        ordemId = UUID.randomUUID();
        envioId = UUID.randomUUID();
        recebimentoId = UUID.randomUUID();

        contaOrigem = new ContaCorrente(contaOrigemId, 12345L, "12345678901");
        contaOrigem.depositar(new BigDecimal("150.00")); // Setup initial balance
        
        contaDestino = new ContaCorrente(contaDestinoId, 54321L, "98765432109");
        contaDestino.depositar(new BigDecimal("50.00")); // Setup initial balance

        command = new TransferenciaCommand("12345-5", "54321-5", new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Deve realizar transferencia com sucesso")
    void deveRealizarTransferenciaComSucesso() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(contaOrigem));
        when(contaCorrenteRepository.encontrarPorNumeroConta(54321L, 5)).thenReturn(Optional.of(contaDestino));
        
        when(idGenerator.nextId()).thenReturn(ordemId, envioId, recebimentoId);
        
        when(ordemTransferenciaRepository.salvar(any(OrdemTransferencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Void result = service.executar(command);

        // Assert
        assertNull(result);
        assertEquals(new BigDecimal("100.00"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("100.00"), contaDestino.getSaldo());
        
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(54321L, 5);
        
        verify(ordemTransferenciaRepository, times(2)).salvar(any(OrdemTransferencia.class));
        
        verify(contaCorrenteRepository, times(2)).salvar(any(ContaCorrente.class));
        
        verify(transacaoRepository, times(1)).salvar(any(TransferenciaEnviada.class));
        verify(transacaoRepository, times(1)).salvar(any(TransferenciaRecebida.class));
        
        verify(idGenerator, times(3)).nextId();
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta origem não existir")
    void deveLancarExcecaoQuandoContaOrigemNaoExistir() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContaCorrenteNaoEncontrada.class, () -> service.executar(command));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, never()).encontrarPorNumeroConta(54321L, 4);
        verify(ordemTransferenciaRepository, never()).salvar(any(OrdemTransferencia.class));
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaEnviada.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaRecebida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta destino não existir")
    void deveLancarExcecaoQuandoContaDestinoNaoExistir() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(contaOrigem));
        when(contaCorrenteRepository.encontrarPorNumeroConta(54321L, 5)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContaCorrenteNaoEncontrada.class, () -> service.executar(command));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(54321L, 5);
        verify(ordemTransferenciaRepository, never()).salvar(any(OrdemTransferencia.class));
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaEnviada.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaRecebida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando número da conta origem for inválido")
    void deveLancarExcecaoQuandoNumeroContaOrigemInvalido() {
        // Arrange
        TransferenciaCommand commandInvalid = new TransferenciaCommand("invalido", "54321-4", new BigDecimal("50.00"));

        // Act & Assert
        assertThrows(NumeroContaInvalidoException.class, () -> service.executar(commandInvalid));
        verify(contaCorrenteRepository, never()).encontrarPorNumeroConta(anyLong(), anyInt());
        verify(ordemTransferenciaRepository, never()).salvar(any(OrdemTransferencia.class));
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaEnviada.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaRecebida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando número da conta destino for inválido")
    void deveLancarExcecaoQuandoNumeroContaDestinoInvalido() {
        // Arrange
        TransferenciaCommand commandInvalid = new TransferenciaCommand("12345-5", "invalido", new BigDecimal("50.00"));
        
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(contaOrigem));

        // Act & Assert
        assertThrows(NumeroContaInvalidoException.class, () -> service.executar(commandInvalid));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(ordemTransferenciaRepository, never()).salvar(any(OrdemTransferencia.class));
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaEnviada.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaRecebida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando saldo da origem for insuficiente")
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(contaOrigem));
        when(contaCorrenteRepository.encontrarPorNumeroConta(54321L, 5)).thenReturn(Optional.of(contaDestino));
        
        when(idGenerator.nextId()).thenReturn(ordemId);
        
        when(ordemTransferenciaRepository.salvar(any(OrdemTransferencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransferenciaCommand commandInsuficiente = new TransferenciaCommand("12345-5", "54321-5", new BigDecimal("200.00"));

        // Act & Assert
        assertThrows(SaldoInsuficienteException.class, () -> service.executar(commandInsuficiente));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(54321L, 5);
        verify(ordemTransferenciaRepository, times(1)).salvar(any(OrdemTransferencia.class));
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaEnviada.class));
        verify(transacaoRepository, never()).salvar(any(TransferenciaRecebida.class));
    }
}