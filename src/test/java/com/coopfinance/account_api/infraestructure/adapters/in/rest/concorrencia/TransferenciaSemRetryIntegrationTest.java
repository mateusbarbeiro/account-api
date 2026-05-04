package com.coopfinance.account_api.infraestructure.adapters.in.rest.concorrencia;

import com.coopfinance.account_api.application.exception.ContaCorrenteAtualizadaConcorrentementeException;
import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarTransferenciaUseCase;
import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Optimistic Lock Test - Transferência Sem Retry")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "resilience4j.retry.instances.transacaoRetry.max-attempts=1"
        })
@Sql(scripts = "/db/db_load_transferencia.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TransferenciaSemRetryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    public static final String NUMERO_CONTA_ORIGEM = "12345-5";
    public static final String NUMERO_CONTA_DESTINO = "54321-5";

    @Test
    void deveFalharImediatamenteAoEfetuarTransferenciasConcorrentesSemRetry() throws InterruptedException {
        int numeroDeThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numeroDeThreads);
        AtomicReference<Exception> excecaoCapturada = new AtomicReference<>();

        Runnable tarefaTransferencia = () -> {
            try {
                startLatch.await();
                TransferenciaCommand input = new TransferenciaCommand(NUMERO_CONTA_ORIGEM, NUMERO_CONTA_DESTINO, BigDecimal.valueOf(50.00));
                realizarTransferenciaUseCase.executar(input);
            } catch (Exception e) {
                excecaoCapturada.set(e);
            } finally {
                doneLatch.countDown();
            }
        };

        for (int i = 0; i < numeroDeThreads; i++) {
            executor.submit(tarefaTransferencia);
        }

        startLatch.countDown(); // Dispara as threads
        doneLatch.await(); // Aguarda finalização

        Exception erro = excecaoCapturada.get();

        // 1. Garante que DEU ERRO (pois o Retry está desabilitado)
        assertNotNull(erro, "Deveria ter ocorrido um erro de concorrência, pois o Retry foi limitado a 1 tentativa.");

        // 2. Valida se o fallback foi acionado corretamente
        assertInstanceOf(ContaCorrenteAtualizadaConcorrentementeException.class, erro, "A exceção principal deveria ser a do Fallback");

        // 3. Valida a causa raiz (O Optimistic Lock do Hibernate)
        assertInstanceOf(OptimisticLockingFailureException.class, erro.getCause(), "A causa raiz deveria ser a exceção de lock do banco de dados");

        executor.shutdown();
    }
}
