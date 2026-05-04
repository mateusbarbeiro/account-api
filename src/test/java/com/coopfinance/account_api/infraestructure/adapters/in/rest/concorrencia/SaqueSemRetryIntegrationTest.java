package com.coopfinance.account_api.infraestructure.adapters.in.rest.concorrencia;

import com.coopfinance.account_api.application.exception.ContaCorrenteAtualizadaConcorrentementeException;
import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarSaqueUseCase;
import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
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

import static com.coopfinance.account_api.infraestructure.adapters.in.rest.ContaCorrenteControllerIntegrationTest.NUMERO_CONTA;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "resilience4j.retry.instances.transacaoRetry.max-attempts=1"
        })
@Sql(scripts = "/db/db_load_saque.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class SaqueSemRetryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RealizarSaqueUseCase realizarSaqueUseCase;

    @Test
    void deveFalharImediatamenteAoEfetuarSaquesConcorrentesSemRetry() throws InterruptedException {
        int numeroDeThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numeroDeThreads);
        AtomicReference<Exception> excecaoCapturada = new AtomicReference<>();

        Runnable tarefaSaque = () -> {
            try {
                startLatch.await();
                SaqueCommand input = new SaqueCommand(NUMERO_CONTA, BigDecimal.valueOf(50.00));
                realizarSaqueUseCase.executar(input);
            } catch (Exception e) {
                excecaoCapturada.set(e);
            } finally {
                doneLatch.countDown();
            }
        };

        for (int i = 0; i < numeroDeThreads; i++) {
            executor.submit(tarefaSaque);
        }

        startLatch.countDown();
        doneLatch.await();

        Exception erro = excecaoCapturada.get();

        assertNotNull(erro, "Deveria ter ocorrido um erro de concorrência, pois o Retry foi limitado a 1 tentativa.");
        assertInstanceOf(ContaCorrenteAtualizadaConcorrentementeException.class, erro, "A exceção principal deveria ser a do Fallback");
        assertInstanceOf(OptimisticLockingFailureException.class, erro.getCause(), "A causa raiz deveria ser a exceção de lock do banco de dados");

        executor.shutdown();
    }
}