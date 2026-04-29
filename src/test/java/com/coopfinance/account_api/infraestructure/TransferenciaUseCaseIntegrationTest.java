package com.coopfinance.account_api.infraestructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class TransferenciaUseCaseIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve bloquear transferência simultânea com lock otimista")
    public void deveBloquearTransferenciaSimultaneaComLockOtimista() {
        // 1. Preparar o cenário: Inserir contas no banco de dados real do contêiner
        // 2. Disparar chamadas assíncronas concorrentes para a mesma transferência
        // 3. Validar se a exceção OptimisticLockException é lançada ou se o Retry agiu corretamente
        // 4. Checar o saldo final da conta no banco de dados
    }
}