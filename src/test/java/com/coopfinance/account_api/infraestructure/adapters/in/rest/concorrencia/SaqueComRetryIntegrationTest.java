package com.coopfinance.account_api.infraestructure.adapters.in.rest.concorrencia;

import com.coopfinance.account_api.application.ports.in.commands.SaqueCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarSaqueUseCase;
import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.ContaCorrenteEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.ContaCorrenteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.coopfinance.account_api.infraestructure.adapters.in.rest.ContaCorrenteControllerIntegrationTest.CONTA_UUID;
import static com.coopfinance.account_api.infraestructure.adapters.in.rest.ContaCorrenteControllerIntegrationTest.NUMERO_CONTA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@Component
@DisplayName("Optimistic Lock Test - Saque")
@Sql(scripts = "/db/db_load_saque.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class SaqueComRetryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RealizarSaqueUseCase realizarSaqueUseCase;

    @Autowired
    private ContaCorrenteJpaRepository contaRepository;

    @Test
    void deveRetentarProcessarSaqueComConcorrenciaAoEfetuarDezSaquesSimultaneos() throws InterruptedException {
        int numeroDeThreads = 10;
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

        ContaCorrenteEntity contaFinal = contaRepository.findById(UUID.fromString(CONTA_UUID)).orElseThrow();

        assertThat(contaFinal.getSaldo()).isEqualByComparingTo(new BigDecimal("500.00")); // Saldo inicial 1000 - (10 * 50) = 500
        assertThat(contaFinal.getVersao()).isEqualTo(10L);
        assertNull(excecaoCapturada.get(), "Nenhuma exceção deveria vazar! O Retry deveria ter resolvido todos os conflitos de Optimistic Lock.");
        executor.shutdown();
    }
}