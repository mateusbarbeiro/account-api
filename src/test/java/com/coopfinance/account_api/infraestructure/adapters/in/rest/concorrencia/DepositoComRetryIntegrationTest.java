package com.coopfinance.account_api.infraestructure.adapters.in.rest.concorrencia;

import com.coopfinance.account_api.application.ports.in.commands.DepositoCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarDepositoUseCase;
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

@DisplayName("Optimistic Lock Test")
@Sql(scripts = "/db/db_load.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class DepositoComRetryIntegrationTest extends BaseIntegrationTest {

    @Autowired
        private RealizarDepositoUseCase realizarDepositoUseCase;

        @Autowired
        private ContaCorrenteJpaRepository contaRepository;

        @Test
        void deveLancarExcecaoDeConcorrenciaAoEfetuarDezDepositosSimultaneos() throws InterruptedException {
            int numeroDeThreads = 10;
            // Pool de 10 threads
            ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);

            // Tiro de largada: sempre 1
            CountDownLatch startLatch = new CountDownLatch(1);
            // Linha de chegada: 10 (uma para cada transação esperada)
            CountDownLatch doneLatch = new CountDownLatch(numeroDeThreads);
            AtomicReference<Exception> excecaoCapturada = new AtomicReference<>();

            Runnable tarefaDeposito = () -> {
                try {
                    startLatch.await(); // As 10 threads travam aqui esperando o sinal

                    // Chamada de depósito
                    DepositoCommand input = new DepositoCommand(NUMERO_CONTA, BigDecimal.valueOf(50.00));
                    startLatch.await();
                    realizarDepositoUseCase.executar(input);

                } catch (Exception e) {
                    excecaoCapturada.set(e);
                } finally {
                    doneLatch.countDown(); // Cada uma das 10 avisa que terminou
                }
            };

            // Submete as 10 tarefas para o executor
            for (int i = 0; i < numeroDeThreads; i++) {
                executor.submit(tarefaDeposito);
            }

            // Dá o tiro de largada. As 10 threads tentam depositar no mesmo milissegundo
            startLatch.countDown();
            // Thread principal aguarda as 10 terminarem
            doneLatch.await();
            // Assert
            ContaCorrenteEntity contaFinal = contaRepository.findById(UUID.fromString(CONTA_UUID)).orElseThrow();

            assertThat(contaFinal.getSaldo()).isEqualByComparingTo(new BigDecimal("500.00")); // Como o Resilience4j está ativo, a thread que falhou buscou o saldo de novo e somou mais 50.00. O saldo final DEVE ser 500.00.
            assertThat(contaFinal.getVersao()).isEqualTo(10L); // O Hibernate incrementou a versão dez vezes (uma para cada commit com sucesso)
            assertNull(excecaoCapturada.get(), "Nenhuma exceção deveria vazar! O Retry deveria ter resolvido todos os conflitos de Optimistic Lock.");
            executor.shutdown();
        }
}
