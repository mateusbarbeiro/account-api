package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.transacao.Deposito;
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
class RealizarDepositoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private IdGenerator idGenerator;

    private RealizarDepositoService service;

    private DepositoCommand command;
    private ContaCorrente conta;
    private UUID transacaoId;

    
    @BeforeEach
    void setUp() {
        service = new RealizarDepositoService(transacaoRepository, contaCorrenteRepository, idGenerator);
        UUID contaId = UUID.randomUUID();
        transacaoId = UUID.randomUUID();

        conta = new ContaCorrente(contaId, 12345L, "12345678901");
        command = new DepositoCommand("12345-5", new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Deve realizar depósito com sucesso")
    void deveRealizarDepositoComSucesso() {
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
        verify(transacaoRepository, times(1)).salvar(any(Deposito.class));
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
        verify(transacaoRepository, never()).salvar(any(Deposito.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando número da conta for inválido")
    void deveLancarExcecaoQuandoNumeroContaInvalido() {
        // Arrange
        DepositoCommand commandInvalid = new DepositoCommand("invalido", new BigDecimal("100.00"));

        // Act & Assert
        assertThrows(NumeroContaInvalidoException.class, () -> service.executar(commandInvalid));
        verify(contaCorrenteRepository, never()).encontrarPorNumeroConta(anyLong(), anyInt());
        verify(contaCorrenteRepository, never()).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, never()).salvar(any(Deposito.class));
    }

    @Test
    @DisplayName("Deve atualizar saldo corretamente após múltiplos depósitos")
    void deveAtualizarSaldoAposMultiplosDepositos() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(12345L, 5)).thenReturn(Optional.of(conta));
        when(idGenerator.nextId()).thenReturn(transacaoId);
        when(contaCorrenteRepository.salvar(any(ContaCorrente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - Primeiro depósito
        service.executar(new DepositoCommand("12345-5", new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), conta.getSaldo());

        // Act - Segundo depósito
        service.executar(new DepositoCommand("12345-5", new BigDecimal("30.00")));

        // Assert
        assertEquals(new BigDecimal("80.00"), conta.getSaldo());
        verify(contaCorrenteRepository, times(2)).encontrarPorNumeroConta(12345L, 5);
        verify(contaCorrenteRepository, times(2)).salvar(any(ContaCorrente.class));
        verify(transacaoRepository, times(2)).salvar(any(Deposito.class));
        verify(idGenerator, times(2)).nextId();
    }
}
