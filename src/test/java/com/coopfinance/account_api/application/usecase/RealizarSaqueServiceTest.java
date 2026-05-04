package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.exception.SaldoInsuficienteException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.transacao.Saque;
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
class RealizarSaqueServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private IdGenerator idGenerator;

    private RealizarSaqueService service;

    private SaqueCommand command;
    private ContaCorrente conta;
    private UUID transacaoId;

    @BeforeEach
    void setUp() {
        service = new RealizarSaqueService(transacaoRepository, contaCorrenteRepository, idGenerator);
        UUID contaId = UUID.randomUUID();
        transacaoId = UUID.randomUUID();

        conta = new ContaCorrente(contaId, 12345L, "12345678901");
        conta.depositar(new BigDecimal("150.00")); // Setup initial balance
        command = new SaqueCommand("12345-5", new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Deve realizar saque com sucesso")
    void deveRealizarSaqueComSucesso() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(conta));
        when(idGenerator.nextId()).thenReturn(transacaoId);
        when(contaCorrenteRepository.salvar(any(ContaCorrente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Void result = service.executar(command);

        // Assert
        assertNull(result);
        assertEquals(new BigDecimal("100.00"), conta.getSaldo());
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, times(1)).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, times(1)).salvar(any(Saque.class));
        verify(idGenerator, times(1)).nextId();
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta não existir")
    void deveLancarExcecaoQuandoContaNaoExistir() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContaCorrenteNaoEncontrada.class, () -> service.executar(command));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(Saque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando número da conta for inválido")
    void deveLancarExcecaoQuandoNumeroContaInvalido() {
        // Arrange
        SaqueCommand commandInvalid = new SaqueCommand("invalido", new BigDecimal("50.00"));

        // Act & Assert
        assertThrows(NumeroContaInvalidoException.class, () -> service.executar(commandInvalid));
        verify(contaCorrenteRepository, never()).encontrarPorNumeroConta(anyLong(), anyInt());
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(Saque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando saldo for insuficiente")
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(conta));
        SaqueCommand commandInsuficiente = new SaqueCommand("12345-5", new BigDecimal("200.00"));

        // Act & Assert
        assertThrows(SaldoInsuficienteException.class, () -> service.executar(commandInsuficiente));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(Saque.class));
    }
    
    @Test
    @DisplayName("Deve atualizar saldo corretamente após múltiplos saques")
    void deveAtualizarSaldoAposMultiplosSaques() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(conta));
        when(idGenerator.nextId()).thenReturn(transacaoId);
        when(contaCorrenteRepository.salvar(any(ContaCorrente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Primeiro saque
        service.executar(new SaqueCommand("12345-5", new BigDecimal("50.00")));
        assertEquals(new BigDecimal("100.00"), conta.getSaldo());

        // Act - Segundo saque
        service.executar(new SaqueCommand("12345-5", new BigDecimal("30.00")));

        // Assert
        assertEquals(new BigDecimal("70.00"), conta.getSaldo());
        verify(contaCorrenteRepository, times(2)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, times(2)).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, times(2)).salvar(any(Saque.class));
        verify(idGenerator, times(2)).nextId();
    }
}
