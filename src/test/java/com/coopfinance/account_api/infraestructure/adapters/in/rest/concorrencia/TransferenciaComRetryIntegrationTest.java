package com.coopfinance.account_api.infraestructure.adapters.in.rest.concorrencia;

import com.coopfinance.account_api.application.ports.in.commands.TransferenciaCommand;
import com.coopfinance.account_api.application.ports.in.usecase.RealizarTransferenciaUseCase;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@Component
@DisplayName("Optimistic Lock Test - Transferência Com Retry")
@Sql(scripts = "/db/db_load_transferencia.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/db/db_clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class TransferenciaComRetryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    @Autowired
    private ContaCorrenteJpaRepository contaRepository;

    public static final String NUMERO_CONTA_ORIGEM = "12345-5";
    public static final String NUMERO_CONTA_DESTINO = "54321-5";
    public static final String CONTA_ORIGEM_UUID = "550e8400-e29b-41d4-a716-446655440000";
    public static final String CONTA_DESTINO_UUID = "550e8400-e29b-41d4-a716-446655440001";

    @Test
    void deveRetentarProcessarTransferenciaComConcorrenciaAoEfetuarDezTransferenciasSimultaneas() throws InterruptedException {
        int numeroDeThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numeroDeThreads);
        AtomicReference<Exception> excecaoCapturada = new AtomicReference<>();

        Runnable tarefaTransferencia = () -> {
            try {
                startLatch.await(); // As threads travam aqui esperando o sinal

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

        startLatch.countDown(); // Libera as threads
        doneLatch.await(); // Aguarda as threads finalizarem

        // Assert
        ContaCorrenteEntity contaOrigemFinal = contaRepository.findById(UUID.fromString(CONTA_ORIGEM_UUID)).orElseThrow();
        ContaCorrenteEntity contaDestinoFinal = contaRepository.findById(UUID.fromString(CONTA_DESTINO_UUID)).orElseThrow();

        // 10 transferencias de 50.00 = 500.00
        // Origem: 1000 - 500 = 500
        // Destino: 1000 + 500 = 1500
        assertThat(contaOrigemFinal.getSaldo()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(contaOrigemFinal.getVersao()).isEqualTo(10L); // Versão incrementada 10 vezes

        assertThat(contaDestinoFinal.getSaldo()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(contaDestinoFinal.getVersao()).isEqualTo(10L); // Versão incrementada 10 vezes

        assertNull(excecaoCapturada.get(), "Nenhuma exceção deveria vazar! O Retry deveria ter resolvido todos os conflitos de Optimistic Lock.");
        executor.shutdown();
    }
}
