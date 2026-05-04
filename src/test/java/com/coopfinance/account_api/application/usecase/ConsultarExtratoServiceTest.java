package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.exception.ContaCorrenteNaoEncontrada;
import com.coopfinance.account_api.application.ports.in.commands.ExtratoCommand;
import com.coopfinance.account_api.application.ports.in.results.ExtratoResult;
import com.coopfinance.account_api.application.ports.in.results.MovimentacaoResult;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.exception.NumeroContaInvalidoException;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import com.coopfinance.account_api.domain.model.transacao.Deposito;
import com.coopfinance.account_api.domain.model.transacao.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ConsultarExtratoServiceTest {
    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ConsultarExtratoService service;

    private ContaCorrente contaCorrente;
    private ExtratoCommand command;

    @BeforeEach
    public void setup() {
        UUID contaId = UUID.randomUUID();
        contaCorrente = new ContaCorrente(contaId, 1L, "12345678901", new BigDecimal("1000.00"), 1L);
        command = new ExtratoCommand("1-8", null, null);
    }

    @Test
    @DisplayName("Deve consultar extrato com sucesso para conta existente")
    void consultaExtratoParaContaExistente() {
        Transacao transacao = new Deposito(
                UUID.randomUUID(),
                contaCorrente,
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("2000.00")
        );

        when(contaCorrenteRepository.encontrarPorNumeroConta(contaCorrente.getNumeroConta(), contaCorrente.getDigitoVerificadorConta()))
                .thenReturn(Optional.of(contaCorrente));
        when(transacaoRepository.listarTransacoesPorConta(any(), any(), any()))
                .thenReturn(new ArrayList<>(Collections.singletonList(new MovimentacaoResult(
                        transacao.getDataHoraTransacao().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
                        "DEPOSITO",
                        transacao.getValorMovimentado(
                )))));

        // Act
        ExtratoResult result = service.executar(command);

        // Assert
        assertNotNull(result);
        assertEquals(0, contaCorrente.getSaldo().compareTo(result.saldoTotal()));

        assertFalse(result.movimentacoes().isEmpty());
        assertEquals(1, result.movimentacoes().size());

        MovimentacaoResult transacaoResult = result.movimentacoes().getFirst();
        assertEquals(transacao.getValorMovimentado(), transacaoResult.valor());
        assertEquals(transacao.tipo().toString(), transacaoResult.tipo());
        assertEquals(transacao.getDataHoraTransacao().atZone(ZoneId.systemDefault()).toOffsetDateTime(), transacaoResult.data());

        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(contaCorrente.getNumeroConta(), contaCorrente.getDigitoVerificadorConta());
        verify(transacaoRepository, times(1)).listarTransacoesPorConta(any(), any(), any());

    }

    @Test
    @DisplayName("Deve consultar extrato com sucesso para conta existente sem transação")
    void consultaExtratoSemTransacoes() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(contaCorrente.getNumeroConta(), contaCorrente.getDigitoVerificadorConta()))
                .thenReturn(Optional.of(contaCorrente));
        when(transacaoRepository.listarTransacoesPorConta(any(), any(), any()))
                .thenReturn(new ArrayList<>(Collections.emptyList()));

        // Act
        ExtratoResult result = service.executar(command);

        // Assert
        assertNotNull(result);
        assertEquals(0, contaCorrente.getSaldo().compareTo(result.saldoTotal()));

        assertTrue(result.movimentacoes().isEmpty());

        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(contaCorrente.getNumeroConta(), contaCorrente.getDigitoVerificadorConta());
        verify(transacaoRepository, times(1)).listarTransacoesPorConta(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar erro consultar extrato para conta inexistente")
    void consultaExtratoContaInexistente() {
        // Arrange
        when(contaCorrenteRepository.encontrarPorNumeroConta(1L, 8)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContaCorrenteNaoEncontrada.class, () -> service.executar(command));
        verify(contaCorrenteRepository, times(1)).encontrarPorNumeroConta(1L, 8);
        verify(transacaoRepository, never()).salvar(any(Deposito.class));
    }

    @Test
    @DisplayName("Deve lançar erro extrato para conta invalida")
    void consultaExtratoContaInvalida() {
        // Arrange
        ExtratoCommand commandInvalid = new ExtratoCommand("invalido", null, null);

        // Act & Assert
        assertThrows(NumeroContaInvalidoException.class, () -> service.executar(commandInvalid));
        verify(contaCorrenteRepository, never()).encontrarPorNumeroConta(anyLong(), anyInt());
        verify(transacaoRepository, never()).salvar(any(Deposito.class));
    }
}
