package com.coopfinance.account_api.application.usecase;

import com.coopfinance.account_api.application.ports.in.commands.AberturaContaCorrenteCommand;
import com.coopfinance.account_api.application.ports.in.results.ContaCorrenteResult;
import com.coopfinance.account_api.application.ports.out.repository.ContaCorrenteRepository;
import com.coopfinance.account_api.application.ports.out.generator.IdGenerator;
import com.coopfinance.account_api.application.ports.out.generator.NumeroContaGenerator;
import com.coopfinance.account_api.application.usecase.mapper.ContaCorrenteUseCaseMapper;
import com.coopfinance.account_api.domain.model.conta.ContaCorrente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AberturaContaCorrenteServiceTest {

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private NumeroContaGenerator numeroContaGenerator;

    @Mock
    private ContaCorrenteUseCaseMapper mapper;

    @InjectMocks
    private AberturaContaCorrenteService service;

    private AberturaContaCorrenteCommand command;
    private UUID generatedId;
    private ContaCorrenteResult expectedResult;

    @BeforeEach
    void setUp() {
        command = new AberturaContaCorrenteCommand("12345678901");
        generatedId = UUID.randomUUID();
        expectedResult = new ContaCorrenteResult("1", "X", "12345678901");
    }

    @Test
    void deveCriarContaComSucessoQuandoNaoExistirNenhumaConta() {
        // Arrange
        when(idGenerator.nextId()).thenReturn(generatedId);
        when(numeroContaGenerator.getNextNumeroConta()).thenReturn(1L);
        when(contaCorrenteRepository.salvar(any(ContaCorrente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResult(any(ContaCorrente.class))).thenReturn(expectedResult);

        // Act
        ContaCorrenteResult result = service.executar(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        
        verify(idGenerator, times(1)).nextId();
        verify(numeroContaGenerator, times(1)).getNextNumeroConta();
        verify(contaCorrenteRepository, times(1)).salvar(argThat(conta -> 
            conta.getId().equals(generatedId) && 
            conta.getNumeroConta().equals(1L) && 
            conta.getDocumento().valor().equals("12345678901")
        ));
        verify(mapper, times(1)).toResult(any(ContaCorrente.class));
    }

    @Test
    void deveCriarContaComProximoNumeroQuandoJaExistirConta() {
        // Arrange
        when(idGenerator.nextId()).thenReturn(generatedId);
        when(numeroContaGenerator.getNextNumeroConta()).thenReturn(101L);
        when(contaCorrenteRepository.salvar(any(ContaCorrente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResult(any(ContaCorrente.class))).thenReturn(expectedResult);

        // Act
        ContaCorrenteResult result = service.executar(command);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        
        verify(idGenerator, times(1)).nextId();
        verify(numeroContaGenerator, times(1)).getNextNumeroConta();
        verify(contaCorrenteRepository, times(1)).salvar(argThat(conta -> 
            conta.getId().equals(generatedId) && 
            conta.getNumeroConta().equals(101L) &&
            conta.getDocumento().valor().equals("12345678901")
        ));
        verify(mapper, times(1)).toResult(any(ContaCorrente.class));
    }
}
